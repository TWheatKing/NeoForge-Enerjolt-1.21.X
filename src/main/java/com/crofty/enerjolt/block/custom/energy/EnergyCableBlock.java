package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Energy Cable Block - Transfers energy between machines
 */
public class EnergyCableBlock extends BaseEntityBlock {
    public static final MapCodec<EnergyCableBlock> CODEC = simpleCodec(EnergyCableBlock::new);

    // Connection properties for each direction
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape CORE_SHAPE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape[] CONNECTION_SHAPES = {
            Block.box(6, 0, 6, 10, 6, 10),  // DOWN
            Block.box(6, 10, 6, 10, 16, 10), // UP
            Block.box(6, 6, 0, 10, 10, 6),   // NORTH
            Block.box(6, 6, 10, 10, 10, 16), // SOUTH
            Block.box(0, 6, 6, 6, 10, 10),   // WEST
            Block.box(10, 6, 6, 16, 10, 10)  // EAST
    };

    private final EnergyTier tier;
    private final int transferRate;

    public EnergyCableBlock(Properties properties, EnergyTier tier) {
        super(properties);
        this.tier = tier;
        this.transferRate = tier.getVoltage();

        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;

        if (state.getValue(DOWN)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[0]);
        if (state.getValue(UP)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[1]);
        if (state.getValue(NORTH)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[2]);
        if (state.getValue(SOUTH)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[3]);
        if (state.getValue(WEST)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[4]);
        if (state.getValue(EAST)) shape = org.minecraft.world.phys.shapes.Shapes.or(shape, CONNECTION_SHAPES[5]);

        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state, tier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EnergyCableBlockEntity cable) {
                cable.tick();
            }
        };
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            updateConnections(level, pos);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide) {
            updateConnections(level, pos);
        }
    }

    private void updateConnections(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof EnergyCableBlockEntity cableEntity) {
            BlockState newState = getBlockState(level, pos);
            if (!newState.equals(level.getBlockState(pos))) {
                level.setBlockAndUpdate(pos, newState);
                cableEntity.updateConnections();
            }
        }
    }

    private BlockState getBlockState(Level level, BlockPos pos) {
        BlockState state = this.defaultBlockState();

        for (Direction direction : Direction.values()) {
            boolean canConnect = canConnectTo(level, pos, direction);
            state = state.setValue(getConnectionProperty(direction), canConnect);
        }

        return state;
    }

    private boolean canConnectTo(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

        // Check if neighbor is an energy handler
        if (neighborEntity instanceof EnergyCapabilityProvider.IEnergyHandler energyHandler) {
            return energyHandler.canConnectEnergy(direction.getOpposite()) &&
                    tier.isCompatibleWith(energyHandler.getEnergyTier());
        }

        // Check if neighbor has energy capability
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                neighborPos, direction.getOpposite());
        return energyStorage != null;
    }

    private BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public EnergyTier getTier() {
        return tier;
    }

    public int getTransferRate() {
        return transferRate;
    }
}

/**
 * Block Entity for Energy Cable
 */
class EnergyCableBlockEntity extends BlockEntity implements EnergyCapabilityProvider.IEnergyHandler {

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
        super(null, pos, state); // Register block entity type
        this.tier = tier;
        this.transferRate = tier.getVoltage();

        // Small buffer to prevent oscillation
        this.buffer = new EnerjoltEnergyStorage(
                transferRate, // Buffer size = transfer rate
                transferRate, // Can receive
                transferRate, // Can extract
                tier,
                true, true, false // No energy loss in cables
        );

        // Initialize capability caches
        energyCaches = new BlockCapabilityCache[6];
        if (level != null) {
            for (Direction direction : Direction.values()) {
                energyCaches[direction.get3DDataValue()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, level, pos.relative(direction), direction.getOpposite());
            }
        }
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
            if (energyCaches[index] != null) {
                energyCaches[index] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, level, neighborPos, direction.getOpposite());
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