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

    public static final Supplier<CreativeModeTab> BISMUTH_ITEMS_TAB = CREATIVE_MODE_TAB.register("bismuth_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BISMUTH.get()))
                    .title(Component.translatable("creativetab.enerjolt.bismuth_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.BISMUTH);
                        output.accept(ModItems.RAW_BISMUTH);

                        output.accept(ModItems.CHISEL);
                        output.accept(ModItems.RADISH);

                        output.accept(ModItems.FROSTFIRE_ICE);
                        output.accept(ModItems.STARLIGHT_ASHES);

                        output.accept(ModItems.BISMUTH_SWORD);
                        output.accept(ModItems.BISMUTH_PICKAXE);
                        output.accept(ModItems.BISMUTH_SHOVEL);
                        output.accept(ModItems.BISMUTH_AXE);
                        output.accept(ModItems.BISMUTH_HOE);

                        //output.accept(ModItems.BISMUTH_HAMMER);

                        output.accept(ModItems.BISMUTH_HELMET);
                        output.accept(ModItems.BISMUTH_CHESTPLATE);
                        output.accept(ModItems.BISMUTH_LEGGINGS);
                        output.accept(ModItems.BISMUTH_BOOTS);

                        //output.accept(ModItems.BISMUTH_HORSE_ARMOR);
                        output.accept(ModItems.KAUPEN_SMITHING_TEMPLATE);

                        output.accept(ModItems.KAUPEN_BOW);

                        output.accept(ModItems.BAR_BRAWL_MUSIC_DISC);
                        output.accept(ModItems.RADISH_SEEDS);

                        output.accept(ModItems.GOJI_BERRIES);
                        output.accept(ModItems.TOMAHAWK);

                        output.accept(ModItems.RADIATION_STAFF);

                        output.accept(ModItems.GECKO_SPAWN_EGG);

                    }).build());

    public static final Supplier<CreativeModeTab> BISMUTH_BLOCK_TAB = CREATIVE_MODE_TAB.register("bismuth_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.BISMUTH_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "bismuth_items_tab"))
                    .title(Component.translatable("creativetab.enerjolt.bismuth_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.BISMUTH_BLOCK);
                        output.accept(ModBlocks.BISMUTH_ORE);
                        output.accept(ModBlocks.BISMUTH_DEEPSLATE_ORE);

                        output.accept(ModBlocks.MAGIC_BLOCK);

                        output.accept(ModBlocks.BISMUTH_STAIRS);
                        output.accept(ModBlocks.BISMUTH_SLAB);

                        output.accept(ModBlocks.BISMUTH_PRESSURE_PLATE);
                        output.accept(ModBlocks.BISMUTH_BUTTON);

                        output.accept(ModBlocks.BISMUTH_FENCE);
                        output.accept(ModBlocks.BISMUTH_FENCE_GATE);
                        output.accept(ModBlocks.BISMUTH_WALL);

                        output.accept(ModBlocks.BISMUTH_DOOR);
                        output.accept(ModBlocks.BISMUTH_TRAPDOOR);

                        output.accept(ModBlocks.BISMUTH_LAMP);

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

    public static final Supplier<CreativeModeTab> MINECRAFT_FARMER_TECH_TAB = CREATIVE_MODE_TAB.register("minecraft_farmer_tech_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHEAT_INGOT.get()))
                    .title(Component.translatable("creativetab.minecraftfarmertechmod.minecraft_farmer_tech"))
                    .displayItems((itemDisplayParameters, output) -> {
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

                        //blocks
                        output.accept(ModBlocks.ZINC_ORE.get());
                        output.accept(ModBlocks.ZINC_DEEPSLATE_ORE.get());
                        output.accept(ModBlocks.ZINC_END_ORE.get());
                        output.accept(ModBlocks.ZINC_NETHER_ORE.get());

                        output.accept(ModBlocks.WHEAT_INGOT_BLOCK.get());
                        output.accept(ModBlocks.ZINC_BLOCK.get());
                        //custom items
                        output.accept(ModItems.WRENCH.get());
                        output.accept(ModItems.CHISEL.get());
                        //output.accept(ModItems.BIO_FUEL_BUCKET.get());
                        //custom blocks
                        //output.accept(ModBlocks.IRON_FURNACE.get());
                        output.accept(ModBlocks.ZINC_CASING.get());
                        // Energy System Blocks
                        //output.accept(ModBlocks.ENERGY_CABLE.get());
                        //output.accept(ModBlocks.COAL_GENERATOR.get());
                        //output.accept(ModBlocks.ENERGY_BATTERY.get());
                        //output.accept(ModBlocks.BIO_GENERATOR.get());
                        //output.accept(ModBlocks.LIQUIFIER.get());
                        //Foods
                        output.accept(ModItems.BURGER.get());
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

                        output.accept(ModItems.ZINC_DRILL);

                        output.accept(ModItems.BAR_BRAWL_MUSIC_DISC);

                        output.accept(ModItems.CORN_SEEDS);
                        output.accept(ModItems.CORN);
                        output.accept(ModItems.STRAWBERRIES);
                        output.accept(ModItems.COTTON_SWAB);
                        output.accept(ModItems.WILDFLOWER);

                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
