package com.crofty.enerjolt.item;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

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

                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
