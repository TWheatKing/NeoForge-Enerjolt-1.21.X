package com.crofty.enerjolt.block;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.custom.energy.*;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registration for all energy-related blocks
 */
public class ModEnergyBlocks {
    public static final DeferredRegister.Blocks ENERGY_BLOCKS =
            DeferredRegister.createBlocks(Enerjolt.MOD_ID);

    // Energy Generators - All Tiers
    public static final DeferredBlock<Block> LV_GENERATOR = registerEnergyBlock("lv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.LOW));
    public static final DeferredBlock<Block> MV_GENERATOR = registerEnergyBlock("mv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> HV_GENERATOR = registerEnergyBlock("hv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.HIGH));
    public static final DeferredBlock<Block> EV_GENERATOR = registerEnergyBlock("ev_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.EXTREME));
    public static final DeferredBlock<Block> IV_GENERATOR = registerEnergyBlock("iv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.INSANE));
    public static final DeferredBlock<Block> LUV_GENERATOR = registerEnergyBlock("luv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.LUDICROUS));
    public static final DeferredBlock<Block> ZV_GENERATOR = registerEnergyBlock("zv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.ZENITH));
    public static final DeferredBlock<Block> UV_GENERATOR = registerEnergyBlock("uv_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.ULTIMATE));
    public static final DeferredBlock<Block> CREATIVE_GENERATOR = registerEnergyBlock("creative_generator",
            () -> new EnergyGeneratorBlock(energyBlockProperties(), EnergyTier.CREATIVE));

    // Energy Storage - All Tiers
    public static final DeferredBlock<Block> LV_ENERGY_STORAGE = registerEnergyBlock("lv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.LOW));
    public static final DeferredBlock<Block> MV_ENERGY_STORAGE = registerEnergyBlock("mv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> HV_ENERGY_STORAGE = registerEnergyBlock("hv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.HIGH));
    public static final DeferredBlock<Block> EV_ENERGY_STORAGE = registerEnergyBlock("ev_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.EXTREME));
    public static final DeferredBlock<Block> IV_ENERGY_STORAGE = registerEnergyBlock("iv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.INSANE));
    public static final DeferredBlock<Block> LUV_ENERGY_STORAGE = registerEnergyBlock("luv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.LUDICROUS));
    public static final DeferredBlock<Block> ZV_ENERGY_STORAGE = registerEnergyBlock("zv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.ZENITH));
    public static final DeferredBlock<Block> UV_ENERGY_STORAGE = registerEnergyBlock("uv_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.ULTIMATE));
    public static final DeferredBlock<Block> CREATIVE_ENERGY_STORAGE = registerEnergyBlock("creative_energy_storage",
            () -> new EnergyStorageBlock(energyBlockProperties(), EnergyTier.CREATIVE));

    // Energy Cables - All Tiers
    public static final DeferredBlock<Block> LV_CABLE = registerEnergyBlock("lv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.LOW));
    public static final DeferredBlock<Block> MV_CABLE = registerEnergyBlock("mv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> HV_CABLE = registerEnergyBlock("hv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.HIGH));
    public static final DeferredBlock<Block> EV_CABLE = registerEnergyBlock("ev_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.EXTREME));
    public static final DeferredBlock<Block> IV_CABLE = registerEnergyBlock("iv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.INSANE));
    public static final DeferredBlock<Block> LUV_CABLE = registerEnergyBlock("luv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.LUDICROUS));
    public static final DeferredBlock<Block> ZV_CABLE = registerEnergyBlock("zv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.ZENITH));
    public static final DeferredBlock<Block> UV_CABLE = registerEnergyBlock("uv_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.ULTIMATE));
    public static final DeferredBlock<Block> CREATIVE_CABLE = registerEnergyBlock("creative_cable",
            () -> new EnergyCableBlock(cableProperties(), EnergyTier.CREATIVE));

    // Transformers
    public static final DeferredBlock<Block> LV_TO_MV_TRANSFORMER = registerEnergyBlock("lv_to_mv_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.LOW, EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> MV_TO_LV_TRANSFORMER = registerEnergyBlock("mv_to_lv_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.MEDIUM, EnergyTier.LOW));
    public static final DeferredBlock<Block> MV_TO_HV_TRANSFORMER = registerEnergyBlock("mv_to_hv_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.MEDIUM, EnergyTier.HIGH));
    public static final DeferredBlock<Block> HV_TO_MV_TRANSFORMER = registerEnergyBlock("hv_to_mv_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.HIGH, EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> HV_TO_EV_TRANSFORMER = registerEnergyBlock("hv_to_ev_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.HIGH, EnergyTier.EXTREME));
    public static final DeferredBlock<Block> EV_TO_HV_TRANSFORMER = registerEnergyBlock("ev_to_hv_transformer",
            () -> new EnergyTransformerBlock(energyBlockProperties(), EnergyTier.EXTREME, EnergyTier.HIGH));
    // ... Add more transformer combinations as needed

    // Advanced Energy Machines
    public static final DeferredBlock<Block> LV_ELECTRIC_SMELTER = registerEnergyBlock("lv_electric_smelter",
            () -> new ElectricSmelterBlock(energyBlockProperties(), EnergyTier.LOW));
    public static final DeferredBlock<Block> MV_ELECTRIC_SMELTER = registerEnergyBlock("mv_electric_smelter",
            () -> new ElectricSmelterBlock(energyBlockProperties(), EnergyTier.MEDIUM));
    public static final DeferredBlock<Block> HV_ELECTRIC_SMELTER = registerEnergyBlock("hv_electric_smelter",
            () -> new ElectricSmelterBlock(energyBlockProperties(), EnergyTier.HIGH));
    public static final DeferredBlock<Block> EV_ELECTRIC_SMELTER = registerEnergyBlock("ev_electric_smelter",
            () -> new ElectricSmelterBlock(energyBlockProperties(), EnergyTier.EXTREME));

    // Ultra-Advanced Machines
    public static final DeferredBlock<Block> QUANTUM_PROCESSOR = registerEnergyBlock("quantum_processor",
            () -> new QuantumProcessorBlock(quantumBlockProperties()));
    public static final DeferredBlock<Block> MATTER_FABRICATOR = registerEnergyBlock("matter_fabricator",
            () -> new MatterFabricatorBlock(quantumBlockProperties()));

    private static BlockBehaviour.Properties energyBlockProperties() {
        return BlockBehaviour.Properties.of()
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    private static BlockBehaviour.Properties cableProperties() {
        return BlockBehaviour.Properties.of()
                .strength(1.0f, 1.0f)
                .noOcclusion()
                .sound(SoundType.METAL);
    }

    private static BlockBehaviour.Properties quantumBlockProperties() {
        return BlockBehaviour.Properties.of()
                .strength(5.0f, 1200.0f) // Explosion resistant
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .lightLevel(state -> 15); // Always glowing
    }

    private static <T extends Block> DeferredBlock<T> registerEnergyBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = ENERGY_BLOCKS.register(name, block);
        registerEnergyBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerEnergyBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ENERGY_BLOCKS.register(eventBus);
    }
}