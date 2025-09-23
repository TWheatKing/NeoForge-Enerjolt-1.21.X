package com.crofty.enerjolt.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Advanced Energy Storage Implementation for Enerjolt
 * Supports multi-tier energy systems with efficiency ratings and loss calculations
 */
public class EnerjoltEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {
    private int energy;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private final boolean canExtract;
    private final boolean canReceive;
    private final EnergyTier tier;
    private float efficiency = 1.0f; // Energy transfer efficiency (0.0-1.0)
    private long lastTransferTime = 0; // For energy loss calculations
    private final boolean hasEnergyLoss;

    // Energy loss rate per second when idle (percentage)
    private static final float BASE_LOSS_RATE = 0.001f; // 0.1% per second

    public EnerjoltEnergyStorage(int capacity, int maxTransfer, EnergyTier tier) {
        this(capacity, maxTransfer, maxTransfer, tier, true, true, true);
    }

    public EnerjoltEnergyStorage(int capacity, int maxReceive, int maxExtract, EnergyTier tier,
                                 boolean canReceive, boolean canExtract, boolean hasEnergyLoss) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.tier = tier;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
        this.hasEnergyLoss = hasEnergyLoss;
        this.energy = 0;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive()) return 0;

        // Apply efficiency loss
        int actualReceive = (int) (toReceive * efficiency);
        int energyReceived = Math.min(capacity - energy, Math.min(maxReceive, actualReceive));

        if (!simulate && energyReceived > 0) {
            energy += energyReceived;
            lastTransferTime = System.currentTimeMillis();
        }

        return (int) (energyReceived / efficiency); // Return original units
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!canExtract()) return 0;

        // Calculate energy loss over time if applicable
        if (hasEnergyLoss && !simulate) {
            applyEnergyLoss();
        }

        int energyExtracted = Math.min(energy, Math.min(maxExtract, toExtract));

        if (!simulate && energyExtracted > 0) {
            energy -= energyExtracted;
            lastTransferTime = System.currentTimeMillis();
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        // Apply energy loss before returning current energy
        if (hasEnergyLoss) {
            applyEnergyLoss();
        }
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    // Custom methods for advanced energy management

    public EnergyTier getTier() {
        return tier;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = Math.max(0.1f, Math.min(1.0f, efficiency));
    }

    public float getStoredPercentage() {
        return capacity > 0 ? (float) energy / capacity : 0;
    }

    public boolean isEmpty() {
        return energy == 0;
    }

    public boolean isFull() {
        return energy >= capacity;
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    private void applyEnergyLoss() {
        if (!hasEnergyLoss || energy == 0) return;

        long currentTime = System.currentTimeMillis();
        if (lastTransferTime == 0) {
            lastTransferTime = currentTime;
            return;
        }

        long timeDelta = currentTime - lastTransferTime;
        if (timeDelta >= 1000) { // Apply loss every second
            float lossRate = BASE_LOSS_RATE * tier.getLossMultiplier();
            int energyLoss = (int) (energy * lossRate * (timeDelta / 1000f));
            energy = Math.max(0, energy - energyLoss);
            lastTransferTime = currentTime;
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", energy);
        tag.putFloat("Efficiency", efficiency);
        tag.putLong("LastTransfer", lastTransferTime);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        energy = tag.getInt("Energy");
        efficiency = tag.getFloat("Efficiency");
        lastTransferTime = tag.getLong("LastTransfer");
    }

    // Helper method for debugging
    public String getEnergyInfo() {
        return String.format("Energy: %d/%d EU (%s) [%.1f%% efficiency]",
                energy, capacity, tier.name(), efficiency * 100);
    }
}