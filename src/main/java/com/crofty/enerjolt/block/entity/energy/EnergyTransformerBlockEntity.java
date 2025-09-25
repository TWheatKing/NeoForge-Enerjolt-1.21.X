package com.crofty.enerjolt.block.entity.energy;

import com.crofty.enerjolt.block.custom.energy.EnergyTransformerBlock;
import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.Nullable; /**
 * Block Entity for Energy Transformer
 */
public class EnergyTransformerBlockEntity extends BlockEntity implements MenuProvider,
        EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier inputTier;
    private final EnergyTier outputTier;
    private final boolean isStepUp;
    private final float conversionEfficiency;
    private final int maxTransformRate;

    // Separate storage for input and output
    private final EnerjoltEnergyStorage inputBuffer;
    private final EnerjoltEnergyStorage outputBuffer;

    // Safety systems
    private int overloadCounter = 0;
    private static final int MAX_OVERLOAD = 100; // 5 seconds at 20 TPS
    private int cooldownTimer = 0;

    // Capability caches
    private BlockCapabilityCache<IEnergyStorage, Direction> inputCache;
    private BlockCapabilityCache<IEnergyStorage, Direction> outputCache;

    public EnergyTransformerBlockEntity(BlockPos pos, BlockState state, EnergyTier inputTier, EnergyTier outputTier) {
        super(null, pos, state); // Register block entity type
        this.inputTier = inputTier;
        this.outputTier = outputTier;
        this.isStepUp = outputTier.ordinal() > inputTier.ordinal();
        this.conversionEfficiency = calculateEfficiency(inputTier, outputTier);
        this.maxTransformRate = calculateTransformRate(inputTier, outputTier);

        // Create input buffer (receives energy from input tier)
        this.inputBuffer = new EnerjoltEnergyStorage(
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

        // Create output buffer (outputs energy at output tier)
        this.outputBuffer = new EnerjoltEnergyStorage(
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

        // Initialize capability caches when level is available
        updateCaches();
    }

    private void updateCaches() {
        if (level != null) {
            Direction facing = getBlockState().getValue(EnergyTransformerBlock.FACING);

            // Input from back, output to front
            BlockPos inputPos = worldPosition.relative(facing.getOpposite());
            BlockPos outputPos = worldPosition.relative(facing);

            inputCache = BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, inputPos, facing);
            outputCache = BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, outputPos, facing.getOpposite());
        }
    }

    private float calculateEfficiency(EnergyTier input, EnergyTier output) {
        int tierDifference = Math.abs(output.ordinal() - input.ordinal());

        return switch (tierDifference) {
            case 0 -> 1.0f;    // Same tier (shouldn't happen)
            case 1 -> 0.95f;   // Adjacent tiers: 95% efficiency
            case 2 -> 0.90f;   // 2 tiers apart: 90% efficiency
            case 3 -> 0.80f;   // 3 tiers apart: 80% efficiency
            default -> 0.70f;  // 4+ tiers apart: 70% efficiency
        };
    }

    private int calculateTransformRate(EnergyTier input, EnergyTier output) {
        // Transform rate is limited by the lower tier's voltage
        return Math.min(input.getVoltage(), output.getVoltage());
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean wasActive = isActive();
        boolean wasOverloaded = isOverloaded();

        // Handle cooldown
        if (cooldownTimer > 0) {
            cooldownTimer--;
            if (cooldownTimer == 0) {
                // Reset overload
                overloadCounter = 0;
                level.setBlockAndUpdate(worldPosition,
                        getBlockState().setValue(EnergyTransformerBlock.OVERLOAD, false));
            }
            return;
        }

        // Pull energy from input side
        pullInputEnergy();

        // Transform energy
        transformEnergy();

        // Push energy to output side
        pushOutputEnergy();

        // Update block state
        updateBlockState(wasActive, wasOverloaded);

        setChanged();
    }

    private void pullInputEnergy() {
        if (inputCache != null && !inputBuffer.isFull()) {
            IEnergyStorage inputSource = inputCache.getCapability();
            if (inputSource != null) {
                int maxPull = inputBuffer.getMaxEnergyStored() - inputBuffer.getEnergyStored();
                int pulled = EnergyCapabilityProvider.EnergyUtils.transferEnergy(
                        inputSource, inputBuffer, Math.min(maxPull, inputTier.getVoltage()));

                // Check for voltage overload
                if (pulled > inputTier.getVoltage() * 2) {
                    overloadCounter++;
                    if (overloadCounter >= MAX_OVERLOAD) {
                        triggerOverload();
                    }
                }
            }
        }
    }

    private void transformEnergy() {
        if (inputBuffer.getEnergyStored() == 0 || outputBuffer.isFull()) return;

        int inputEnergy = Math.min(inputBuffer.getEnergyStored(), maxTransformRate);
        if (inputEnergy == 0) return;

        // Calculate transformed energy with efficiency loss
        int transformedEnergy = (int) (inputEnergy * conversionEfficiency);

        // For step-up transformers, require more input energy for same output
        if (isStepUp) {
            int ratio = outputTier.getVoltage() / inputTier.getVoltage();
            if (inputEnergy < ratio) return; // Not enough energy to step up

            inputEnergy = Math.min(inputEnergy, ratio * (outputBuffer.getMaxEnergyStored() - outputBuffer.getEnergyStored()));
            transformedEnergy = inputEnergy / ratio;
        } else {
            // Step-down: can output more energy than input
            int ratio = inputTier.getVoltage() / outputTier.getVoltage();
            transformedEnergy = Math.min(transformedEnergy * ratio,
                    outputBuffer.getMaxEnergyStored() - outputBuffer.getEnergyStored());
        }

        if (transformedEnergy > 0) {
            // Consume input energy
            inputBuffer.extractEnergy(inputEnergy, false);

            // Produce output energy
            outputBuffer.receiveEnergy(transformedEnergy, false);

            // Play transformation sound
            if (level.getGameTime() % 20 == 0) { // Once per second
                level.playSound(null, worldPosition, SoundEvents.BEACON_AMBIENT,
                        SoundSource.BLOCKS, 0.1f, isStepUp ? 1.2f : 0.8f);
            }
        }
    }

    private void pushOutputEnergy() {
        if (outputCache != null && outputBuffer.getEnergyStored() > 0) {
            IEnergyStorage outputTarget = outputCache.getCapability();
            if (outputTarget != null) {
                EnergyCapabilityProvider.EnergyUtils.transferEnergy(
                        outputBuffer, outputTarget, outputTier.getVoltage());
            }
        }
    }

    private void updateBlockState(boolean wasActive, boolean wasOverloaded) {
        boolean isActiveNow = isActive();
        boolean isOverloadedNow = isOverloaded();

        if (wasActive != isActiveNow || wasOverloaded != isOverloadedNow) {
            level.setBlockAndUpdate(worldPosition, getBlockState()
                    .setValue(EnergyTransformerBlock.ACTIVE, isActiveNow)
                    .setValue(EnergyTransformerBlock.OVERLOAD, isOverloadedNow));
        }
    }

    private void triggerOverload() {
        // Transformer overloads - stops working and needs cooldown
        cooldownTimer = 200; // 10 seconds

        level.setBlockAndUpdate(worldPosition,
                getBlockState().setValue(EnergyTransformerBlock.OVERLOAD, true));

        // Play overload sound
        level.playSound(null, worldPosition, SoundEvents.REDSTONE_TORCH_BURNOUT,
                SoundSource.BLOCKS, 1.0f, 0.5f);

        // Clear buffers
        inputBuffer.setEnergyStored(0);
        outputBuffer.setEnergyStored(0);

        // Small explosion effect
        level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                1.0f, Level.ExplosionInteraction.NONE);
    }

    private boolean isActive() {
        return inputBuffer.getEnergyStored() > 0 || outputBuffer.getEnergyStored() > 0;
    }

    private boolean isOverloaded() {
        return cooldownTimer > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("InputBuffer", inputBuffer.serializeNBT(registries));
        tag.put("OutputBuffer", outputBuffer.serializeNBT(registries));
        tag.putInt("OverloadCounter", overloadCounter);
        tag.putInt("CooldownTimer", cooldownTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inputBuffer.deserializeNBT(registries, tag.getCompound("InputBuffer"));
        outputBuffer.deserializeNBT(registries, tag.getCompound("OutputBuffer"));
        overloadCounter = tag.getInt("OverloadCounter");
        cooldownTimer = tag.getInt("CooldownTimer");
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        updateCaches();
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        String type = isStepUp ? "step_up" : "step_down";
        return Component.translatable("block.enerjolt.energy_transformer_" + type,
                inputTier.getShortName(), outputTier.getShortName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Return transformer menu
        return null; // Implementation depends on your GUI system
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        if (direction == null) return null;

        Direction facing = getBlockState().getValue(EnergyTransformerBlock.FACING);

        // Input from back
        if (direction == facing.getOpposite()) {
            return new EnergyCapabilityProvider.DirectionalEnergyStorage(
                    inputBuffer, direction, true, false);
        }

        // Output to front
        if (direction == facing) {
            return new EnergyCapabilityProvider.DirectionalEnergyStorage(
                    outputBuffer, direction, false, true);
        }

        return null; // No connections on sides
    }

    @Override
    public EnergyTier getEnergyTier() {
        return outputTier; // Report output tier as primary
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        if (direction == null) return false;

        Direction facing = getBlockState().getValue(EnergyTransformerBlock.FACING);
        return direction == facing || direction == facing.getOpposite();
    }

    // Public getters for GUI
    public EnerjoltEnergyStorage getInputBuffer() {
        return inputBuffer;
    }

    public EnerjoltEnergyStorage getOutputBuffer() {
        return outputBuffer;
    }

    public EnergyTier getInputTier() {
        return inputTier;
    }

    public EnergyTier getOutputTier() {
        return outputTier;
    }

    public float getConversionEfficiency() {
        return conversionEfficiency;
    }

    public boolean isStepUp() {
        return isStepUp;
    }

    public int getOverloadProgress() {
        return (overloadCounter * 100) / MAX_OVERLOAD;
    }

    public int getCooldownProgress() {
        return cooldownTimer > 0 ? (cooldownTimer * 100) / 200 : 0;
    }
}
