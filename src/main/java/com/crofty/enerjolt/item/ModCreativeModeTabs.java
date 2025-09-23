package com.crofty.enerjolt.item;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.crofty.enerjolt.block.ModEnergyBlocks;
import com.crofty.enerjolt.energy.EnergyTier;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Enerjolt.MOD_ID);

    public static final Supplier<CreativeModeTab> ENERJOLT_ITEMS_TAB = CREATIVE_MODE_TAB.register("enerjolt_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ZINC_INGOT.get()))
                    .title(Component.translatable("creativetab.enerjolt.bismuth_items"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //custom items
                        output.accept(ModItems.WRENCH.get());
                        output.accept(ModItems.CHISEL.get());
                        //output.accept(ModItems.BIO_FUEL_BUCKET.get());
                        //food related ingot
                        output.accept(ModItems.WHEAT_INGOT.get());
                        //machine related items
                        output.accept(ModItems.ZINC_INGOT.get());
                        output.accept(ModItems.RAW_ZINC.get());
                        output.accept(ModItems.ZINC_ALLOY.get());
                        output.accept(ModItems.BRASS.get());
                        output.accept(ModItems.ANDESITE_ALLOY.get());
                        //bits
                        output.accept(ModItems.IRON_BIT.get());
                        output.accept(ModItems.GOLD_BIT.get());
                        output.accept(ModItems.COPPER_BIT.get());
                        output.accept(ModItems.DIAMOND_BIT.get());
                        output.accept(ModItems.NETHERITE_BIT.get());
                        //cbs
                        //output.accept(ModItems.BASIC_CIRCUIT_BOARD.get());
                        //output.accept(ModItems.ADVANCED_CIRCUIT_BOARD.get());
                        //output.accept(ModItems.FUSION_CIRCUIT_BOARD.get());
                        //machine parts
                        output.accept(ModItems.BASIC_BLADE.get());
                        output.accept(ModItems.ADVANCED_BLADE.get());
                        output.accept(ModItems.GRATE.get());
                        output.accept(ModItems.WISK.get());
                        output.accept(ModItems.RAM.get());
                        output.accept(ModItems.EMPTY_COIL.get());
                        output.accept(ModItems.COPPER_COIL.get());
                        output.accept(ModItems.GOLD_COIL.get());
                        output.accept(ModItems.DIAMOND_COIL.get());
                        //plates
                        output.accept(ModItems.IRON_PLATE.get());
                        output.accept(ModItems.GOLD_PLATE.get());
                        output.accept(ModItems.BRASS_PLATE.get());
                        output.accept(ModItems.DIAMOND_PLATE.get());
                        output.accept(ModItems.NETHERITE_PLATE.get());
                        //custom tools
                        output.accept(ModItems.ZINC_SWORD);
                        output.accept(ModItems.ZINC_PICKAXE);
                        output.accept(ModItems.ZINC_SHOVEL);
                        output.accept(ModItems.ZINC_AXE);
                        output.accept(ModItems.ZINC_HOE);
                        output.accept(ModItems.ZINC_BOW);

                        //armor
                        output.accept(ModItems.ZINC_HELMET.get());
                        output.accept(ModItems.ZINC_CHESTPLATE.get());
                        output.accept(ModItems.ZINC_LEGGINGS.get());
                        output.accept(ModItems.ZINC_BOOTS.get());

                        output.accept(ModItems.ZINC_HORSE_ARMOR.get());

                        output.accept(ModItems.ZINC_SMITHING_TEMPLATE);
                        output.accept(ModItems.CHISEL);
                        output.accept(ModItems.RADISH);
                        //Foods
                        output.accept(ModItems.BURGER.get());

                        output.accept(ModItems.ZINC_DRILL);

                        output.accept(ModItems.CORN_SEEDS);
                        output.accept(ModItems.CORN);
                        output.accept(ModItems.STRAWBERRIES);
                        output.accept(ModItems.COTTON_SWAB);
                        output.accept(ModItems.WILDFLOWER);

                        output.accept(ModItems.BAR_BRAWL_MUSIC_DISC);
                        output.accept(ModItems.TOMAHAWK);

                        output.accept(ModItems.RADIATION_STAFF);

                        output.accept(ModItems.GECKO_SPAWN_EGG);

                        // Add Energy Tools
                        output.accept(ModEnergyItems.LV_DRILL);
                        output.accept(ModEnergyItems.MV_DRILL);
                        output.accept(ModEnergyItems.HV_DRILL);
                        output.accept(ModEnergyItems.EV_DRILL);

                        // Add Energy Batteries
                        output.accept(ModEnergyItems.LV_BATTERY);
                        output.accept(ModEnergyItems.MV_BATTERY);
                        output.accept(ModEnergyItems.HV_BATTERY);
                        output.accept(ModEnergyItems.EV_BATTERY);

                        // Add Advanced Energy Items
                        output.accept(ModEnergyItems.ENERGY_MULTITOOL);
                        output.accept(ModEnergyItems.ENERGY_SCANNER);
                        output.accept(ModEnergyItems.ENERGY_DEBUGGER);

                    }).build());

    public static final Supplier<CreativeModeTab> ENERJOLT_BLOCK_TAB = CREATIVE_MODE_TAB.register("enerjolt_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ZINC_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "enerjolt_items_tab"))
                    .title(Component.translatable("creativetab.enerjolt.bismuth_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        //blocks
                        output.accept(ModBlocks.ZINC_ORE.get());
                        output.accept(ModBlocks.ZINC_DEEPSLATE_ORE.get());
                        output.accept(ModBlocks.ZINC_END_ORE.get());
                        output.accept(ModBlocks.ZINC_NETHER_ORE.get());

                        output.accept(ModBlocks.WHEAT_INGOT_BLOCK.get());
                        output.accept(ModBlocks.ZINC_BLOCK.get());

                        //custom blocks
                        //output.accept(ModBlocks.IRON_FURNACE.get());
                        output.accept(ModBlocks.ZINC_CASING.get());
                        // Energy System Blocks
                        //output.accept(ModBlocks.ENERGY_CABLE.get());
                        //output.accept(ModBlocks.COAL_GENERATOR.get());
                        //output.accept(ModBlocks.ENERGY_BATTERY.get());
                        //output.accept(ModBlocks.BIO_GENERATOR.get());
                        //output.accept(ModBlocks.LIQUIFIER.get());

                        //Non-Block Blocks
                        output.accept(ModBlocks.ZINC_STAIRS.get());
                        output.accept(ModBlocks.ZINC_SLAB.get());

                        output.accept(ModBlocks.ZINC_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.ZINC_BUTTON.get());

                        output.accept(ModBlocks.ZINC_FENCE.get());
                        output.accept(ModBlocks.ZINC_FENCE_GATE.get());
                        output.accept(ModBlocks.ZINC_WALL.get());

                        output.accept(ModBlocks.ZINC_DOOR.get());
                        output.accept(ModBlocks.ZINC_TRAPDOOR.get());

                        output.accept(ModBlocks.ZINC_LAMP.get());

                        output.accept(ModBlocks.BLOODWOOD_LOG.get());
                        output.accept(ModBlocks.BLOODWOOD_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_BLOODWOOD_LOG.get());
                        output.accept(ModBlocks.STRIPPED_BLOODWOOD_WOOD.get());

                        output.accept(ModBlocks.BLOODWOOD_PLANKS.get());
                        output.accept(ModBlocks.BLOODWOOD_SAPLING.get());

                        output.accept(ModBlocks.BLOODWOOD_LEAVES.get());

                        output.accept(ModBlocks.CHAIR.get());
                        output.accept(ModBlocks.PEDESTAL.get());

                        output.accept(ModBlocks.GROWTH_CHAMBER.get());

                        // Energy Generators
                        output.accept(ModEnergyBlocks.LV_GENERATOR);
                        output.accept(ModEnergyBlocks.MV_GENERATOR);
                        output.accept(ModEnergyBlocks.HV_GENERATOR);
                        output.accept(ModEnergyBlocks.EV_GENERATOR);
                        output.accept(ModEnergyBlocks.CREATIVE_GENERATOR);

                        // Energy Storage
                        output.accept(ModEnergyBlocks.LV_ENERGY_STORAGE);
                        output.accept(ModEnergyBlocks.MV_ENERGY_STORAGE);
                        output.accept(ModEnergyBlocks.HV_ENERGY_STORAGE);
                        output.accept(ModEnergyBlocks.EV_ENERGY_STORAGE);
                        output.accept(ModEnergyBlocks.CREATIVE_ENERGY_STORAGE);

                        // Energy Cables
                        output.accept(ModEnergyBlocks.LV_CABLE);
                        output.accept(ModEnergyBlocks.MV_CABLE);
                        output.accept(ModEnergyBlocks.HV_CABLE);
                        output.accept(ModEnergyBlocks.EV_CABLE);
                        output.accept(ModEnergyBlocks.CREATIVE_CABLE);

                        // Transformers
                        output.accept(ModEnergyBlocks.LV_TO_MV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.MV_TO_LV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.MV_TO_HV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.HV_TO_MV_TRANSFORMER);

                        // Advanced Machines
                        output.accept(ModEnergyBlocks.LV_ELECTRIC_SMELTER);
                        output.accept(ModEnergyBlocks.MV_ELECTRIC_SMELTER);
                        output.accept(ModEnergyBlocks.HV_ELECTRIC_SMELTER);

                        // Ultimate Machines
                        output.accept(ModEnergyBlocks.QUANTUM_PROCESSOR);
                        output.accept(ModEnergyBlocks.MATTER_FABRICATOR);

                    }).build());

    public static final Supplier<CreativeModeTab> ENERGY_TAB = CREATIVE_MODE_TAB.register("energy_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModEnergyBlocks.MV_GENERATOR.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "enerjolt_blocks_tab"))
                    .title(Component.translatable("creativetab.enerjolt.energy"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Generators by tier
                        addEnergyBlocksByTier(output, "generator", ModEnergyBlocks.LV_GENERATOR,
                                ModEnergyBlocks.MV_GENERATOR, ModEnergyBlocks.HV_GENERATOR,
                                ModEnergyBlocks.EV_GENERATOR, ModEnergyBlocks.IV_GENERATOR,
                                ModEnergyBlocks.LUV_GENERATOR, ModEnergyBlocks.ZV_GENERATOR,
                                ModEnergyBlocks.UV_GENERATOR, ModEnergyBlocks.CREATIVE_GENERATOR);

                        // Storage by tier
                        addEnergyBlocksByTier(output, "storage", ModEnergyBlocks.LV_ENERGY_STORAGE,
                                ModEnergyBlocks.MV_ENERGY_STORAGE, ModEnergyBlocks.HV_ENERGY_STORAGE,
                                ModEnergyBlocks.EV_ENERGY_STORAGE, ModEnergyBlocks.IV_ENERGY_STORAGE,
                                ModEnergyBlocks.LUV_ENERGY_STORAGE, ModEnergyBlocks.ZV_ENERGY_STORAGE,
                                ModEnergyBlocks.UV_ENERGY_STORAGE, ModEnergyBlocks.CREATIVE_ENERGY_STORAGE);

                        // Cables by tier
                        addEnergyBlocksByTier(output, "cable", ModEnergyBlocks.LV_CABLE,
                                ModEnergyBlocks.MV_CABLE, ModEnergyBlocks.HV_CABLE,
                                ModEnergyBlocks.EV_CABLE, ModEnergyBlocks.IV_CABLE,
                                ModEnergyBlocks.LUV_CABLE, ModEnergyBlocks.ZV_CABLE,
                                ModEnergyBlocks.UV_CABLE, ModEnergyBlocks.CREATIVE_CABLE);

                        // Tools by tier
                        addEnergyItemsByTier(output, "drill", ModEnergyItems.LV_DRILL,
                                ModEnergyItems.MV_DRILL, ModEnergyItems.HV_DRILL,
                                ModEnergyItems.EV_DRILL, ModEnergyItems.IV_DRILL,
                                ModEnergyItems.LUV_DRILL, ModEnergyItems.ZV_DRILL,
                                ModEnergyItems.UV_DRILL, ModEnergyItems.CREATIVE_DRILL);

                        // Batteries by tier
                        addEnergyItemsByTier(output, "battery", ModEnergyItems.LV_BATTERY,
                                ModEnergyItems.MV_BATTERY, ModEnergyItems.HV_BATTERY,
                                ModEnergyItems.EV_BATTERY, ModEnergyItems.IV_BATTERY,
                                ModEnergyItems.LUV_BATTERY, ModEnergyItems.ZV_BATTERY,
                                ModEnergyItems.UV_BATTERY, ModEnergyItems.CREATIVE_BATTERY);

                        // Transformers
                        output.accept(ModEnergyBlocks.LV_TO_MV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.MV_TO_LV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.MV_TO_HV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.HV_TO_MV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.HV_TO_EV_TRANSFORMER);
                        output.accept(ModEnergyBlocks.EV_TO_HV_TRANSFORMER);

                        // Advanced Machines
                        output.accept(ModEnergyBlocks.LV_ELECTRIC_SMELTER);
                        output.accept(ModEnergyBlocks.MV_ELECTRIC_SMELTER);
                        output.accept(ModEnergyBlocks.HV_ELECTRIC_SMELTER);
                        output.accept(ModEnergyBlocks.EV_ELECTRIC_SMELTER);

                        // Ultimate Technology
                        output.accept(ModEnergyBlocks.QUANTUM_PROCESSOR);
                        output.accept(ModEnergyBlocks.MATTER_FABRICATOR);

                        // Debug Tools
                        output.accept(ModEnergyItems.ENERGY_SCANNER);
                        output.accept(ModEnergyItems.ENERGY_DEBUGGER);
                        output.accept(ModEnergyItems.ENERGY_MULTITOOL);

                    }).build());

    @SafeVarargs
    private static void addEnergyBlocksByTier(CreativeModeTab.Output output, String type,
                                              DeferredBlock<Block>... blocks) {
        for (DeferredBlock<Block> block : blocks) {
            output.accept(block);
        }
    }

    @SafeVarargs
    private static void addEnergyItemsByTier(CreativeModeTab.Output output, String type,
                                             DeferredItem<Item>... items) {
        for (DeferredItem<Item> item : items) {
            output.accept(item);
        }
    }


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
