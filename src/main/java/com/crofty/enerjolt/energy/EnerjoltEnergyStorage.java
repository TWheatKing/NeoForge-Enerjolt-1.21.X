package com.crofty.enerjolt.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Extended energy storage interface for Enerjolt mod
 * Combines IEnergyStorage with advanced energy management capabilities
 */
public interface EnerjoltEnergyStorage extends IEnergyStorage, INBTSerializable<CompoundTag> {

    /**
     * Gets the energy tier of this storage
     * @return The energy tier
     */
    EnergyTier getTier();

    /**
     * Gets the energy efficiency multiplier
     * @return Efficiency as a float between 0.0 and 1.0
     */
    float getEfficiency();

    /**
     * Sets the energy efficiency multiplier
     * @param efficiency Efficiency as a float between 0.0 and 1.0
     */
    void setEfficiency(float efficiency);

    /**
     * Gets the stored energy as a percentage of capacity
     * @return Percentage from 0.0 to 1.0
     */
    float getStoredPercentage();

    /**
     * Checks if the storage is empty
     * @return true if no energy is stored
     */
    boolean isEmpty();

    /**
     * Checks if the storage is full
     * @return true if at maximum capacity
     */
    boolean isFull();

    /**
     * Sets the energy stored directly
     * @param energy The energy amount to set
     */
    void setEnergyStored(int energy);

    /**
     * Gets debug information about this energy storage
     * @return Formatted string with energy info
     */
    default String getEnergyInfo() {
        return String.format("Energy: %d/%d EU (%s) [%.1f%% efficiency]",
                getEnergyStored(), getMaxEnergyStored(), getTier().name(), getEfficiency() * 100);
    }

    /**
     * Called when energy storage changes
     * Default implementation does nothing - override for notifications
     */
    default void onEnergyChanged() {
        // Default: do nothing
    }

    // INBTSerializable methods - must be implemented
    @Override
    CompoundTag serializeNBT(HolderLookup.Provider provider);

    @Override
    void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt);
}