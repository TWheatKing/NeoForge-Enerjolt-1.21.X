package com.crofty.enerjolt.energy;

import net.minecraft.nbt.CompoundTag;

/**
 * Default implementation of the Enerjolt energy storage system
 */
public class EnergyStorageImpl implements EnerjoltEnergyStorage {

    protected int energy;
    protected int capacity;
    protected int maxInput;
    protected int maxOutput;
    protected EnergyTier tier;
    protected Runnable onEnergyChangedCallback;

    public EnergyStorageImpl(int capacity, int maxTransfer, EnergyTier tier) {
        this(capacity, maxTransfer, maxTransfer, tier);
    }

    public EnergyStorageImpl(int capacity, int maxInput, int maxOutput, EnergyTier tier) {
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
        this.tier = tier;
    }

    public EnergyStorageImpl(int capacity, EnergyTier tier) {
        this(capacity, tier.getMaxTransfer(), tier.getMaxTransfer(), tier);
    }

    /**
     * Set a callback for when energy changes
     */
    public EnergyStorageImpl setEnergyChangedCallback(Runnable callback) {
        this.onEnergyChangedCallback = callback;
        return this;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxInput, toReceive));
        if (!simulate) {
            energy += energyReceived;
            onEnergyChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxOutput, toExtract));
        if (!simulate) {
            energy -= energyExtracted;
            onEnergyChanged();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxOutput > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxInput > 0;
    }

    @Override
    public EnergyTier getTier() {
        return tier;
    }

    @Override
    public int getMaxInput() {
        return maxInput;
    }

    @Override
    public int getMaxOutput() {
        return maxOutput;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
        onEnergyChanged();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("energy", energy);
        nbt.putInt("capacity", capacity);
        nbt.putInt("maxInput", maxInput);
        nbt.putInt("maxOutput", maxOutput);
        nbt.putString("tier", tier.getSerializedName());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.energy = nbt.getInt("energy");
        this.capacity = nbt.getInt("capacity");
        this.maxInput = nbt.getInt("maxInput");
        this.maxOutput = nbt.getInt("maxOutput");
        if (nbt.contains("tier")) {
            String tierName = nbt.getString("tier");
            for (EnergyTier tier : EnergyTier.values()) {
                if (tier.getSerializedName().equals(tierName)) {
                    this.tier = tier;
                    break;
                }
            }
        }
        onEnergyChanged();
    }

    @Override
    public void onEnergyChanged() {
        if (onEnergyChangedCallback != null) {
            onEnergyChangedCallback.run();
        }
    }

    /**
     * Add energy directly (for generators)
     */
    public void addEnergy(int amount) {
        this.energy = Math.min(capacity, this.energy + amount);
        onEnergyChanged();
    }

    /**
     * Remove energy directly (for consumers)
     */
    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
        onEnergyChanged();
    }

    /**
     * Check if we have at least the specified amount of energy
     */
    public boolean hasEnergy(int amount) {
        return this.energy >= amount;
    }

    /**
     * Try to consume energy, returns true if successful
     */
    public boolean tryConsumeEnergy(int amount) {
        if (hasEnergy(amount)) {
            consumeEnergy(amount);
            return true;
        }
        return false;
    }

    /**
     * Get energy that can be extracted this tick
     */
    public int getEnergyToExtract() {
        return Math.min(energy, maxOutput);
    }

    /**
     * Get energy that can be received this tick
     */
    public int getEnergyToReceive() {
        return Math.min(capacity - energy, maxInput);
    }
}