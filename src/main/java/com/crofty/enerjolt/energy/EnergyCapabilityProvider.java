package com.crofty.enerjolt.energy;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.entity.ModEnergyBlockEntities;
import com.crofty.enerjolt.item.ModEnergyItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Handles registration and management of energy capabilities for Enerjolt
 */
@EventBusSubscriber(modid = Enerjolt.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EnergyCapabilityProvider {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register energy capabilities for energy tools and items
        registerEnergyItems(event);

        // Register energy capabilities for energy blocks
        registerEnergyBlocks(event);
    }

    private static void registerEnergyItems(RegisterCapabilitiesEvent event) {
        // Register capability for items that have ENERGY_DATA component
        event.registerItem(Capabilities.EnergyStorage.ITEM,
                (stack, context) -> {
                    var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
                    if (energyData != null) {
                        var config = stack.getOrDefault(ModEnergyComponents.ENERGY_CONFIG.get(),
                                ModEnergyComponents.EnergyConfig.createDefault(energyData.tier()));

                        return new ComponentEnergyStorage(stack, ModEnergyComponents.ENERGY.get(),
                                energyData.capacity(), config.maxReceive(), config.maxExtract()) {

                            @Override
                            public boolean canReceive() {
                                return config.canReceive();
                            }

                            @Override
                            public boolean canExtract() {
                                return config.canExtract();
                            }

                            @Override
                            public int receiveEnergy(int toReceive, boolean simulate) {
                                var currentData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
                                if (currentData == null) return 0;

                                // Apply efficiency
                                int actualReceive = (int) (toReceive * currentData.efficiency());
                                int result = super.receiveEnergy(actualReceive, simulate);

                                // Update energy data if not simulating
                                if (!simulate && result > 0) {
                                    int newEnergy = Math.min(currentData.capacity(),
                                            currentData.energy() + result);
                                    stack.set(ModEnergyComponents.ENERGY_DATA.get(),
                                            currentData.withEnergy(newEnergy));
                                }

                                return (int) (result / currentData.efficiency());
                            }

                            @Override
                            public int extractEnergy(int toExtract, boolean simulate) {
                                var currentData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
                                if (currentData == null) return 0;

                                int result = super.extractEnergy(toExtract, simulate);

                                // Update energy data if not simulating
                                if (!simulate && result > 0) {
                                    int newEnergy = Math.max(0, currentData.energy() - result);
                                    stack.set(ModEnergyComponents.ENERGY_DATA.get(),
                                            currentData.withEnergy(newEnergy));
                                }

                                return result;
                            }

                            @Override
                            public int getEnergyStored() {
                                var currentData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
                                return currentData != null ? currentData.energy() : 0;
                            }

                            @Override
                            public int getMaxEnergyStored() {
                                var currentData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
                                return currentData != null ? currentData.capacity() : 0;
                            }
                        };
                    }
                    return null;
                },
                // ALL ENERGY ITEMS - Batteries
                ModEnergyItems.LV_BATTERY.get(),
                ModEnergyItems.MV_BATTERY.get(),
                ModEnergyItems.HV_BATTERY.get(),
                ModEnergyItems.EV_BATTERY.get(),
                ModEnergyItems.IV_BATTERY.get(),
                ModEnergyItems.LUV_BATTERY.get(),
                ModEnergyItems.ZV_BATTERY.get(),
                ModEnergyItems.UV_BATTERY.get(),
                ModEnergyItems.CREATIVE_BATTERY.get(),

                // Energy Drills
                ModEnergyItems.LV_DRILL.get(),
                ModEnergyItems.MV_DRILL.get(),
                ModEnergyItems.HV_DRILL.get(),
                ModEnergyItems.EV_DRILL.get(),
                ModEnergyItems.IV_DRILL.get(),
                ModEnergyItems.LUV_DRILL.get(),
                ModEnergyItems.ZV_DRILL.get(),
                ModEnergyItems.UV_DRILL.get(),
                ModEnergyItems.CREATIVE_DRILL.get(),

                // Other Energy Items
                ModEnergyItems.ENERGY_MULTITOOL.get(),
                ModEnergyItems.ENERGY_SCANNER.get(),
                ModEnergyItems.ENERGY_DEBUGGER.get()
        );
    }

    private static void registerEnergyBlocks(RegisterCapabilitiesEvent event) {
        // Register for energy generator block entity
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ModEnergyBlockEntities.ENERGY_GENERATOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof IEnergyHandler handler) {
                        return handler.getEnergyStorage(direction);
                    }
                    return null;
                });

        // Add more block entity registrations as needed for your other energy blocks
        // when you create their block entities...
    }

    /**
     * Interface for block entities that handle energy
     */
    public interface IEnergyHandler {
        IEnergyStorage getEnergyStorage(@Nullable Direction direction);
        EnergyTier getEnergyTier();
        boolean canConnectEnergy(@Nullable Direction direction);
    }

    /**
     * Wrapper for directional energy access
     */
    public static class DirectionalEnergyStorage implements IEnergyStorage {
        private final EnerjoltEnergyStorage internal;
        private final Direction allowedDirection;
        private final boolean canReceive;
        private final boolean canExtract;

        public DirectionalEnergyStorage(EnerjoltEnergyStorage internal,
                                        @Nullable Direction allowedDirection,
                                        boolean canReceive, boolean canExtract) {
            this.internal = internal;
            this.allowedDirection = allowedDirection;
            this.canReceive = canReceive;
            this.canExtract = canExtract;
        }

        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            if (!canReceive()) return 0;
            return internal.receiveEnergy(toReceive, simulate);
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            if (!canExtract()) return 0;
            return internal.extractEnergy(toExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return internal.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return internal.getMaxEnergyStored();
        }

        @Override
        public boolean canReceive() {
            return canReceive && internal.canReceive();
        }

        @Override
        public boolean canExtract() {
            return canExtract && internal.canExtract();
        }
    }

    /**
     * Multi-directional energy storage for complex machines
     */
    public static class MultiDirectionalEnergyStorage implements IEnergyStorage {
        private final EnerjoltEnergyStorage internal;
        private final Direction queryDirection;
        private final boolean[] canReceiveFrom = new boolean[6]; // DUNSWE order
        private final boolean[] canExtractFrom = new boolean[6];

        public MultiDirectionalEnergyStorage(EnerjoltEnergyStorage internal, Direction queryDirection) {
            this.internal = internal;
            this.queryDirection = queryDirection;

            // Default: allow all directions
            for (int i = 0; i < 6; i++) {
                canReceiveFrom[i] = true;
                canExtractFrom[i] = true;
            }
        }

        public void setCanReceive(Direction direction, boolean canReceive) {
            if (direction != null) {
                canReceiveFrom[direction.get3DDataValue()] = canReceive;
            }
        }

        public void setCanExtract(Direction direction, boolean canExtract) {
            if (direction != null) {
                canExtractFrom[direction.get3DDataValue()] = canExtract;
            }
        }

        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            if (!canReceive()) return 0;
            return internal.receiveEnergy(toReceive, simulate);
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            if (!canExtract()) return 0;
            return internal.extractEnergy(toExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return internal.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return internal.getMaxEnergyStored();
        }

        @Override
        public boolean canReceive() {
            if (queryDirection == null) return internal.canReceive();
            return canReceiveFrom[queryDirection.get3DDataValue()] && internal.canReceive();
        }

        @Override
        public boolean canExtract() {
            if (queryDirection == null) return internal.canExtract();
            return canExtractFrom[queryDirection.get3DDataValue()] && internal.canExtract();
        }
    }

    /**
     * Utility methods for energy operations
     */
    public static class EnergyUtils {

        /**
         * Transfer energy between two storage objects
         */
        public static int transferEnergy(IEnergyStorage from, IEnergyStorage to, int maxTransfer) {
            if (!from.canExtract() || !to.canReceive()) return 0;

            int energyToTransfer = from.extractEnergy(maxTransfer, true);
            if (energyToTransfer == 0) return 0;

            int energyReceived = to.receiveEnergy(energyToTransfer, true);
            if (energyReceived == 0) return 0;

            // Perform actual transfer
            from.extractEnergy(energyReceived, false);
            return to.receiveEnergy(energyReceived, false);
        }

        /**
         * Get energy stored percentage as float (0.0 to 1.0)
         */
        public static float getEnergyPercentage(IEnergyStorage storage) {
            int max = storage.getMaxEnergyStored();
            if (max == 0) return 0.0f;
            return (float) storage.getEnergyStored() / max;
        }

        /**
         * Check if energy storage is full
         */
        public static boolean isFull(IEnergyStorage storage) {
            return storage.getEnergyStored() >= storage.getMaxEnergyStored();
        }

        /**
         * Check if energy storage is empty
         */
        public static boolean isEmpty(IEnergyStorage storage) {
            return storage.getEnergyStored() == 0;
        }

        /**
         * Get energy tier from ItemStack
         */
        public static EnergyTier getEnergyTier(ItemStack stack) {
            var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
            if (energyData != null) {
                return energyData.tier();
            }

            var tier = stack.get(ModEnergyComponents.ENERGY_TIER.get());
            return tier != null ? tier : EnergyTier.LOW;
        }

        /**
         * Check if two energy tiers are compatible for direct connection
         */
        public static boolean areCompatible(EnergyTier tier1, EnergyTier tier2) {
            return tier1.isCompatibleWith(tier2);
        }
    }
}