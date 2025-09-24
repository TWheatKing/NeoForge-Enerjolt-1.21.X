package com.crofty.enerjolt.block.entity.energy;

import com.crofty.enerjolt.block.entity.ModEnergyBlockEntities;
import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Energy Storage Block Entity - Stores energy for later use
 */
public class EnergyStorageBlockEntity extends BlockEntity implements MenuProvider,
        EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier tier;
    private final EnerjoltEnergyStorage energyStorage;
    private int lastChargeLevel = -1;

    // Capability caches for adjacent blocks
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] energyCaches;

    @SuppressWarnings("unchecked")
    public EnergyStorageBlockEntity(BlockPos pos, BlockState state, EnergyTier tier) {
        super(ModEnergyBlockEntities.ENERGY_STORAGE_BE.get(), pos, state);
        this.tier = tier;

        // Create large energy storage
        int capacity = calculateCapacity(tier);
        int transferRate = tier.getVoltage() * 2; // Can charge/discharge at 2x voltage

        this.energyStorage = new EnerjoltEnergyStorage(
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
                return null;
            }

            @Override
            public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

            }
        };

        // Initialize capability caches
        energyCaches = new BlockCapabilityCache[6];
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                energyCaches[direction.get3DDataValue()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, serverLevel, pos.relative(direction), direction.getOpposite());
            }
        }
    }

    private int calculateCapacity(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 32000;      // 32k EU
            case MEDIUM -> 128000;  // 128k EU
            case HIGH -> 512000;    // 512k EU
            case EXTREME -> 2048000; // 2M EU
            case INSANE -> 8192000; // 8M EU
            case LUDICROUS -> 32768000; // 32M EU
            case ZENITH -> 131072000; // 131M EU
            case ULTIMATE -> 524288000; // 524M EU
            case CREATIVE -> Integer.MAX_VALUE; // Infinite
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Auto-output energy to adjacent consumers if configured
        distributeEnergyToAdjacent();

        // Update visual charge level
        updateChargeLevel();

        setChanged();
    }

    private void distributeEnergyToAdjacent() {
        if (energyStorage.getEnergyStored() == 0) return;

        int maxTransfer = tier.getVoltage();

        for (Direction direction : Direction.values()) {
            if (energyCaches[direction.get3DDataValue()] != null) {
                IEnergyStorage consumer = energyCaches[direction.get3DDataValue()].getCapability();
                if (consumer != null && consumer.canReceive()) {
                    EnergyCapabilityProvider.EnergyUtils.transferEnergy(
                            energyStorage, consumer, maxTransfer);
                }
            }
        }
    }

    private void updateChargeLevel() {
        int newChargeLevel = (int) (energyStorage.getStoredPercentage() * 8);
        if (newChargeLevel != lastChargeLevel) {
            lastChargeLevel = newChargeLevel;
            // Update block state with charge level (implement in your block class)
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        // Reinitialize caches when level is set
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                energyCaches[direction.get3DDataValue()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, serverLevel, worldPosition.relative(direction), direction.getOpposite());
            }
        }
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.enerjolt.energy_storage_" + tier.name().toLowerCase());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Return your storage menu here
        return null; // Implementation depends on your GUI system
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }

    @Override
    public EnergyTier getEnergyTier() {
        return tier;
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        return true;
    }

    // Public getters
    public EnerjoltEnergyStorage getEnergyStorageInternal() {
        return energyStorage;
    }

    public EnergyTier getTier() {
        return tier;
    }

    public int getChargeLevel() {
        return lastChargeLevel;
    }
}