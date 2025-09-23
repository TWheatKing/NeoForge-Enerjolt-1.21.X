package com.crofty.enerjolt.block.entity;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.ModEnergyBlocks;
import com.crofty.enerjolt.block.custom.energy.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registration for Energy Block Entity Types
 */
public class ModEnergyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> ENERGY_BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Enerjolt.MOD_ID);

    // Energy Generators
    public static final Supplier<BlockEntityType<EnergyGeneratorBlockEntity>> ENERGY_GENERATOR_BE =
            ENERGY_BLOCK_ENTITIES.register("energy_generator_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        // Determine tier from block
                        if (state.getBlock() instanceof EnergyGeneratorBlock generatorBlock) {
                            return new EnergyGeneratorBlockEntity(pos, state, generatorBlock.getTier());
                        }
                        return new EnergyGeneratorBlockEntity(pos, state, com.crofty.enerjolt.energy.EnergyTier.LOW);
                    },
                    ModEnergyBlocks.LV_GENERATOR.get(),
                    ModEnergyBlocks.MV_GENERATOR.get(),
                    ModEnergyBlocks.HV_GENERATOR.get(),
                    ModEnergyBlocks.EV_GENERATOR.get(),
                    ModEnergyBlocks.IV_GENERATOR.get(),
                    ModEnergyBlocks.LUV_GENERATOR.get(),
                    ModEnergyBlocks.ZV_GENERATOR.get(),
                    ModEnergyBlocks.UV_GENERATOR.get(),
                    ModEnergyBlocks.CREATIVE_GENERATOR.get()
            ).build(null));

    // Energy Storage
    public static final Supplier<BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE_BE =
            ENERGY_BLOCK_ENTITIES.register("energy_storage_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        // Determine tier from block
                        if (state.getBlock() instanceof EnergyStorageBlock storageBlock) {
                            return new EnergyStorageBlockEntity(pos, state, storageBlock.getTier());
                        }
                        return new EnergyStorageBlockEntity(pos, state, com.crofty.enerjolt.energy.EnergyTier.LOW);
                    },
                    ModEnergyBlocks.LV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.MV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.HV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.EV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.IV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.LUV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.ZV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.UV_ENERGY_STORAGE.get(),
                    ModEnergyBlocks.CREATIVE_ENERGY_STORAGE.get()
            ).build(null));

    // Energy Cables
    public static final Supplier<BlockEntityType<EnergyCableBlockEntity>> ENERGY_CABLE_BE =
            ENERGY_BLOCK_ENTITIES.register("energy_cable_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        // Determine tier from block
                        if (state.getBlock() instanceof EnergyCableBlock cableBlock) {
                            return new EnergyCableBlockEntity(pos, state, cableBlock.getTier());
                        }
                        return new EnergyCableBlockEntity(pos, state, com.crofty.enerjolt.energy.EnergyTier.LOW);
                    },
                    ModEnergyBlocks.LV_CABLE.get(),
                    ModEnergyBlocks.MV_CABLE.get(),
                    ModEnergyBlocks.HV_CABLE.get(),
                    ModEnergyBlocks.EV_CABLE.get(),
                    ModEnergyBlocks.IV_CABLE.get(),
                    ModEnergyBlocks.LUV_CABLE.get(),
                    ModEnergyBlocks.ZV_CABLE.get(),
                    ModEnergyBlocks.UV_CABLE.get(),
                    ModEnergyBlocks.CREATIVE_CABLE.get()
            ).build(null));

    // Energy Transformers
    public static final Supplier<BlockEntityType<EnergyTransformerBlockEntity>> ENERGY_TRANSFORMER_BE =
            ENERGY_BLOCK_ENTITIES.register("energy_transformer_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        // Determine input/output tiers from block
                        if (state.getBlock() instanceof EnergyTransformerBlock transformerBlock) {
                            return new EnergyTransformerBlockEntity(pos, state,
                                    transformerBlock.getInputTier(), transformerBlock.getOutputTier());
                        }
                        return new EnergyTransformerBlockEntity(pos, state,
                                com.crofty.enerjolt.energy.EnergyTier.LOW, com.crofty.enerjolt.energy.EnergyTier.MEDIUM);
                    },
                    ModEnergyBlocks.LV_TO_MV_TRANSFORMER.get(),
                    ModEnergyBlocks.MV_TO_LV_TRANSFORMER.get(),
                    ModEnergyBlocks.MV_TO_HV_TRANSFORMER.get(),
                    ModEnergyBlocks.HV_TO_MV_TRANSFORMER.get(),
                    ModEnergyBlocks.HV_TO_EV_TRANSFORMER.get(),
                    ModEnergyBlocks.EV_TO_HV_TRANSFORMER.get()
            ).build(null));

    // Electric Smelters
    public static final Supplier<BlockEntityType<ElectricSmelterBlockEntity>> ELECTRIC_SMELTER_BE =
            ENERGY_BLOCK_ENTITIES.register("electric_smelter_be", () -> BlockEntityType.Builder.of(
                    (pos, state) -> {
                        // Determine tier from block
                        if (state.getBlock() instanceof ElectricSmelterBlock smelterBlock) {
                            return new ElectricSmelterBlockEntity(pos, state, smelterBlock.getTier());
                        }
                        return new ElectricSmelterBlockEntity(pos, state, com.crofty.enerjolt.energy.EnergyTier.LOW);
                    },
                    ModEnergyBlocks.LV_ELECTRIC_SMELTER.get(),
                    ModEnergyBlocks.MV_ELECTRIC_SMELTER.get(),
                    ModEnergyBlocks.HV_ELECTRIC_SMELTER.get(),
                    ModEnergyBlocks.EV_ELECTRIC_SMELTER.get()
            ).build(null));

    // Quantum Processor
    public static final Supplier<BlockEntityType<QuantumProcessorBlockEntity>> QUANTUM_PROCESSOR_BE =
            ENERGY_BLOCK_ENTITIES.register("quantum_processor_be", () -> BlockEntityType.Builder.of(
                    QuantumProcessorBlockEntity::new,
                    ModEnergyBlocks.QUANTUM_PROCESSOR.get()
            ).build(null));

    // Matter Fabricator (when implemented)
    /*
    public static final Supplier<BlockEntityType<MatterFabricatorBlockEntity>> MATTER_FABRICATOR_BE =
            ENERGY_BLOCK_ENTITIES.register("matter_fabricator_be", () -> BlockEntityType.Builder.of(
                    MatterFabricatorBlockEntity::new,
                    ModEnergyBlocks.MATTER_FABRICATOR.get()
            ).build(null));
    */

    public static void register(IEventBus eventBus) {
        ENERGY_BLOCK_ENTITIES.register(eventBus);
    }
}

// Update the existing block entity classes to use the registered types

// FILE: Update EnergyGeneratorBlockEntity
/*
// In EnergyGeneratorBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.ENERGY_GENERATOR_BE.get(), pos, state);
*/

// FILE: Update EnergyStorageBlockEntity
/*
// In EnergyStorageBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.ENERGY_STORAGE_BE.get(), pos, state);
*/

// FILE: Update EnergyCableBlockEntity
/*
// In EnergyCableBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.ENERGY_CABLE_BE.get(), pos, state);
*/

// FILE: Update EnergyTransformerBlockEntity
/*
// In EnergyTransformerBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.ENERGY_TRANSFORMER_BE.get(), pos, state);
*/

// FILE: Update ElectricSmelterBlockEntity
/*
// In ElectricSmelterBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.ELECTRIC_SMELTER_BE.get(), pos, state);
*/

// FILE: Update QuantumProcessorBlockEntity
/*
// In QuantumProcessorBlockEntity constructor, replace:
// super(null, pos, state);
// With:
super(ModEnergyBlockEntities.QUANTUM_PROCESSOR_BE.get(), pos, state);
*/

// FILE: Register block entities in main mod class
// Add to Enerjolt.java constructor:
/*
// Register Energy Block Entities
ModEnergyBlockEntities.register(modEventBus);
*/

// FILE: Client-side block entity renderer registration
// Add to ClientModEvents in Enerjolt.java:
/*
@SubscribeEvent
public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
    // ... existing renderers ...

    // Energy block entity renderers
    event.registerBlockEntityRenderer(ModEnergyBlockEntities.ENERGY_GENERATOR_BE.get(),
        EnergyGeneratorRenderer::new);
    event.registerBlockEntityRenderer(ModEnergyBlockEntities.ENERGY_STORAGE_BE.get(),
        EnergyStorageRenderer::new);
    event.registerBlockEntityRenderer(ModEnergyBlockEntities.ENERGY_CABLE_BE.get(),
        EnergyCableRenderer::new);
    event.registerBlockEntityRenderer(ModEnergyBlockEntities.ENERGY_TRANSFORMER_BE.get(),
        EnergyTransformerRenderer::new);
    event.registerBlockEntityRenderer(ModEnergyBlockEntities.QUANTUM_PROCESSOR_BE.get(),
        QuantumProcessorRenderer::new);
}
*/

/**
 * Enhanced Growth Chamber Integration with Energy Support
 * Update your existing ModBlockEntities.java to support energy
 */
/*
// Modify existing GROWTH_CHAMBER_BE to support energy:
public static final Supplier<BlockEntityType<GrowthChamberBlockEntity>> GROWTH_CHAMBER_BE =
        BLOCK_ENTITIES.register("growth_chamber_be", () -> BlockEntityType.Builder.of(
                EnhancedGrowthChamberBlockEntity::new, ModBlocks.GROWTH_CHAMBER.get()).build(null));
*/

/**
 * Enhanced Growth Chamber with Energy Support
 * This extends your existing GrowthChamberBlockEntity
 */
class EnhancedGrowthChamberBlockEntity extends com.crofty.enerjolt.block.entity.GrowthChamberBlockEntity
        implements com.crofty.enerjolt.energy.EnergyCapabilityProvider.IEnergyHandler {

    private final com.crofty.enerjolt.energy.EnerjoltEnergyStorage energyStorage;
    private boolean useEnergy = false;
    private static final int ENERGY_PER_TICK = 10; // 10 EU/t when powered

    public EnhancedGrowthChamberBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockState) {
        super(pos, blockState);

        // Add energy storage capability
        this.energyStorage = new com.crofty.enerjolt.energy.EnerjoltEnergyStorage(
                10000, // 10k EU capacity
                com.crofty.enerjolt.energy.EnergyTier.LOW.getVoltage(), // Can receive LV
                0, // Cannot output energy
                com.crofty.enerjolt.energy.EnergyTier.LOW,
                true, false, false // Can receive, cannot extract, no loss
        );
    }

    @Override
    public void tick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos blockPos,
                     net.minecraft.world.level.block.state.BlockState blockState) {
        if (hasRecipe()) {
            boolean hasEnergy = energyStorage.getEnergyStored() >= ENERGY_PER_TICK;

            if (hasEnergy && useEnergy) {
                // Energy-powered mode: 3x speed
                energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                increaseCraftingProgress(3);

                // Particle effects when energy-powered
                if (level.isClientSide && level.random.nextFloat() < 0.3f) {
                    level.addParticle(com.crofty.enerjolt.particle.ModParticles.BISMUTH_PARTICLES.get(),
                            blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5,
                            0, 0.1, 0);
                }
            } else {
                // Normal mode: standard speed
                increaseCraftingProgress();
            }

            setChanged(level, blockPos, blockState);

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putBoolean("UseEnergy", useEnergy);
    }

    @Override
    protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        useEnergy = tag.getBoolean("UseEnergy");
    }

    // IEnergyHandler implementation
    @Override
    public net.neoforged.neoforge.energy.IEnergyStorage getEnergyStorage(@org.jetbrains.annotations.Nullable net.minecraft.core.Direction direction) {
        return new com.crofty.enerjolt.energy.EnergyCapabilityProvider.DirectionalEnergyStorage(
                energyStorage, direction, true, false);
    }

    @Override
    public com.crofty.enerjolt.energy.EnergyTier getEnergyTier() {
        return com.crofty.enerjolt.energy.EnergyTier.LOW;
    }

    @Override
    public boolean canConnectEnergy(@org.jetbrains.annotations.Nullable net.minecraft.core.Direction direction) {
        return direction != net.minecraft.core.Direction.DOWN; // No energy from bottom
    }

    // Public getters for GUI
    public com.crofty.enerjolt.energy.EnerjoltEnergyStorage getEnergyStorageInternal() {
        return energyStorage;
    }

    public boolean isUsingEnergy() {
        return useEnergy;
    }

    public void setUseEnergy(boolean useEnergy) {
        this.useEnergy = useEnergy;
        setChanged();
    }

    public int getEnergyProgress() {
        return (int) (energyStorage.getStoredPercentage() * 100);
    }
}