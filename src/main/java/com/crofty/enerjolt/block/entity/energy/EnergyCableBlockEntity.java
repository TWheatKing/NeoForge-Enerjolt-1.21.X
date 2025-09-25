package com.crofty.enerjolt.block.entity.energy;

import com.crofty.enerjolt.block.entity.ModEnergyBlockEntities;
import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Energy Cable Block Entity - Transfers energy between machines
 */
public class EnergyCableBlockEntity extends BlockEntity implements EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier tier;
    private final int transferRate;
    private final EnerjoltEnergyStorage buffer;

    // Connection tracking
    private final boolean[] connections = new boolean[6]; // DUNSWE
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] energyCaches;

    // Cable network optimization
    private CableNetwork network;
    private int networkUpdateTimer = 0;

    @SuppressWarnings("unchecked")
    public EnergyCableBlockEntity(BlockPos pos, BlockState state, EnergyTier tier) {
        super(ModEnergyBlockEntities.ENERGY_CABLE_BE.get(), pos, state);
        this.tier = tier;
        this.transferRate = tier.getVoltage();

        // Small buffer to prevent oscillation
        this.buffer = new EnerjoltEnergyStorage(
                // No energy loss in cables
        ) {
            @Override
            public int receiveEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public int extractEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return 0;
            }

            @Override
            public int getMaxEnergyStored() {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public EnergyTier getTier() {
                return null;
            }

            @Override
            public float getEfficiency() {
                return 0;
            }

            @Override
            public void setEfficiency(float efficiency) {

            }

            @Override
            public float getStoredPercentage() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean isFull() {
                return false;
            }

            @Override
            public void setEnergyStored(int energy) {

            }

            @Override
            public CompoundTag serializeNBT(HolderLookup.Provider provider) {
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("Energy", this.getEnergyStored());
                return nbt;
            }

            @Override
            public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
                if (tag.contains("Energy")) {
                    this.setEnergyStored(tag.getInt("Energy")); // ✅ correct method
                }
            }

        };

        // Initialize capability caches
        energyCaches = new BlockCapabilityCache[6];
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Update network periodically
        if (++networkUpdateTimer % 20 == 0) { // Every second
            updateNetwork();
        }

        // Transfer energy through cable network
        transferEnergyThroughNetwork();

        setChanged();
    }

    private void transferEnergyThroughNetwork() {
        if (network == null) return;

        // Collect all energy sources and sinks
        List<EnergyNode> sources = new ArrayList<>();
        List<EnergyNode> sinks = new ArrayList<>();

        for (BlockPos cablePos : network.getCablePositions()) {
            if (level.getBlockEntity(cablePos) instanceof EnergyCableBlockEntity cable) {
                cable.collectEnergyNodes(sources, sinks);
            }
        }

        // Distribute energy from sources to sinks
        distributeEnergy(sources, sinks);
    }

    private void collectEnergyNodes(List<EnergyNode> sources, List<EnergyNode> sinks) {
        for (Direction direction : Direction.values()) {
            if (!connections[direction.get3DDataValue()]) continue;

            IEnergyStorage storage = energyCaches[direction.get3DDataValue()].getCapability();
            if (storage == null) continue;

            if (storage.canExtract() && storage.getEnergyStored() > 0) {
                sources.add(new EnergyNode(worldPosition.relative(direction), storage, direction.getOpposite()));
            }

            if (storage.canReceive() && storage.getEnergyStored() < storage.getMaxEnergyStored()) {
                sinks.add(new EnergyNode(worldPosition.relative(direction), storage, direction.getOpposite()));
            }
        }
    }

    private void distributeEnergy(List<EnergyNode> sources, List<EnergyNode> sinks) {
        if (sources.isEmpty() || sinks.isEmpty()) return;

        int totalAvailable = sources.stream()
                .mapToInt(node -> node.storage.extractEnergy(transferRate, true))
                .sum();

        if (totalAvailable == 0) return;

        int totalCapacity = sinks.stream()
                .mapToInt(node -> node.storage.receiveEnergy(transferRate, true))
                .sum();

        int energyToDistribute = Math.min(totalAvailable, totalCapacity);
        if (energyToDistribute == 0) return;

        // Calculate distribution ratios
        for (EnergyNode sink : sinks) {
            int canReceive = sink.storage.receiveEnergy(transferRate, true);
            if (canReceive == 0) continue;

            float ratio = (float) canReceive / totalCapacity;
            int energyForThisSink = (int) (energyToDistribute * ratio);

            // Find sources to draw from
            int remaining = energyForThisSink;
            for (EnergyNode source : sources) {
                if (remaining <= 0) break;

                int extracted = source.storage.extractEnergy(remaining, false);
                if (extracted > 0) {
                    int received = sink.storage.receiveEnergy(extracted, false);
                    remaining -= received;

                    // Return any excess energy
                    if (received < extracted) {
                        source.storage.receiveEnergy(extracted - received, false);
                    }
                }
            }
        }
    }

    public void updateConnections() {
        if (level == null) return;

        for (Direction direction : Direction.values()) {
            int index = direction.get3DDataValue();
            BlockPos neighborPos = worldPosition.relative(direction);

            // Update connection status
            connections[index] = canConnectInDirection(direction);

            // Update capability cache
            if (level instanceof ServerLevel serverLevel) {
                energyCaches[index] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, serverLevel, neighborPos, direction.getOpposite());
            }
        }

        // Mark network for update
        if (network != null) {
            network.markDirty();
        }
    }

    private boolean canConnectInDirection(Direction direction) {
        if (level == null) return false;

        BlockPos neighborPos = worldPosition.relative(direction);
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

        // Check if neighbor is compatible energy handler
        if (neighborEntity instanceof EnergyCapabilityProvider.IEnergyHandler energyHandler) {
            return energyHandler.canConnectEnergy(direction.getOpposite()) &&
                    tier.isCompatibleWith(energyHandler.getEnergyTier());
        }

        // Check for energy capability
        IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                neighborPos, direction.getOpposite());
        return storage != null;
    }

    private void updateNetwork() {
        if (network == null || network.isDirty()) {
            network = CableNetwork.buildNetwork(level, worldPosition, tier);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", buffer.serializeNBT(registries));

        // Save connections
        for (int i = 0; i < 6; i++) {
            tag.putBoolean("connection_" + i, connections[i]);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        buffer.deserializeNBT(registries, tag.getCompound("Buffer"));

        // Load connections
        for (int i = 0; i < 6; i++) {
            connections[i] = tag.getBoolean("connection_" + i);
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        updateConnections();
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return buffer; // Cables act as pass-through with small buffer
    }

    @Override
    public EnergyTier getEnergyTier() {
        return tier;
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        if (direction == null) return true;
        return connections[direction.get3DDataValue()];
    }

    public boolean isConnected(Direction direction) {
        return connections[direction.get3DDataValue()];
    }

    public EnergyTier getTier() {
        return tier;
    }

    /**
     * Represents an energy source or sink in the network
     */
    private static class EnergyNode {
        final BlockPos pos;
        final IEnergyStorage storage;
        final Direction side;

        EnergyNode(BlockPos pos, IEnergyStorage storage, Direction side) {
            this.pos = pos;
            this.storage = storage;
            this.side = side;
        }
    }
}

/**
 * Cable Network for optimized energy distribution
 */
class CableNetwork {
    private final Set<BlockPos> cablePositions = new HashSet<>();
    private final EnergyTier tier;
    private boolean dirty = false;

    private CableNetwork(EnergyTier tier) {
        this.tier = tier;
    }

    public static CableNetwork buildNetwork(Level level, BlockPos startPos, EnergyTier tier) {
        CableNetwork network = new CableNetwork(tier);
        network.buildFromPosition(level, startPos, new HashSet<>());
        return network;
    }

    private void buildFromPosition(Level level, BlockPos pos, Set<BlockPos> visited) {
        if (visited.contains(pos)) return;
        visited.add(pos);

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof EnergyCableBlockEntity cable)) return;
        if (!cable.getEnergyTier().equals(tier)) return;

        cablePositions.add(pos);

        // Recursively find connected cables
        for (Direction direction : Direction.values()) {
            if (cable.isConnected(direction)) {
                BlockPos neighborPos = pos.relative(direction);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (neighbor instanceof EnergyCableBlockEntity) {
                    buildFromPosition(level, neighborPos, visited);
                }
            }
        }
    }

    public Set<BlockPos> getCablePositions() {
        return cablePositions;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }
}