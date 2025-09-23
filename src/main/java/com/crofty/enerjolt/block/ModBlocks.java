package com.crofty.enerjolt.block;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.custom.*;
import com.crofty.enerjolt.item.ModItems;
import com.crofty.enerjolt.sound.ModSounds;
import com.crofty.enerjolt.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Enerjolt.MOD_ID);

    //starter examples
    public static final DeferredBlock<Block> BISMUTH_BLOCK = registerBlock("bismuth_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> BISMUTH_ORE = registerBlock("bismuth_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BISMUTH_DEEPSLATE_ORE = registerBlock("bismuth_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6),
                    BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> BISMUTH_END_ORE = registerBlock("bismuth_end_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 9),
                    BlockBehaviour.Properties.of().strength(7f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> BISMUTH_NETHER_ORE = registerBlock("bismuth_nether_ore",
            () -> new DropExperienceBlock(UniformInt.of(1, 5),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));


    public static final DeferredBlock<Block> MAGIC_BLOCK = registerBlock("magic_block",
            () -> new MagicBlock(BlockBehaviour.Properties.of().strength(2f).noLootTable().sound(ModSounds.MAGIC_BLOCK_SOUNDS)));

    public static final DeferredBlock<StairBlock> BISMUTH_STAIRS = registerBlock("bismuth_stairs",
            () -> new StairBlock(ModBlocks.BISMUTH_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> BISMUTH_SLAB = registerBlock("bismuth_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<PressurePlateBlock> BISMUTH_PRESSURE_PLATE = registerBlock("bismuth_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<ButtonBlock> BISMUTH_BUTTON = registerBlock("bismuth_button",
            () -> new ButtonBlock(BlockSetType.IRON, 20, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noCollission()));

    public static final DeferredBlock<FenceBlock> BISMUTH_FENCE = registerBlock("bismuth_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FenceGateBlock> BISMUTH_FENCE_GATE = registerBlock("bismuth_fence_gate",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<WallBlock> BISMUTH_WALL = registerBlock("bismuth_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<DoorBlock> BISMUTH_DOOR = registerBlock("bismuth_door",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> BISMUTH_TRAPDOOR = registerBlock("bismuth_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));

    public static final DeferredBlock<Block> BISMUTH_LAMP = registerBlock("bismuth_lamp",
            () -> new BismuthLampBlock(BlockBehaviour.Properties.of().strength(2f)
                    .requiresCorrectToolForDrops().lightLevel(state -> state.getValue(BismuthLampBlock.CLICKED) ? 15 : 0)));

    public static final DeferredBlock<Block> RADISH_CROP = BLOCKS.register("radish_crop",
            () -> new RadishCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEETROOTS)));

    public static final DeferredBlock<Block> GOJI_BERRY_BUSH = BLOCKS.register("goji_berry_bush",
            () -> new GojiBerryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));


    public static final DeferredBlock<Block> BLOODWOOD_LOG = registerBlock("bloodwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredBlock<Block> BLOODWOOD_WOOD = registerBlock("bloodwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));
    public static final DeferredBlock<Block> STRIPPED_BLOODWOOD_LOG = registerBlock("stripped_bloodwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));
    public static final DeferredBlock<Block> STRIPPED_BLOODWOOD_WOOD = registerBlock("stripped_bloodwood_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));

    public static final DeferredBlock<Block> BLOODWOOD_PLANKS = registerBlock("bloodwood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final DeferredBlock<Block> BLOODWOOD_LEAVES = registerBlock("bloodwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> BLOODWOOD_SAPLING = registerBlock("bloodwood_sapling",
            () -> new ModSaplingBlock(ModTreeGrowers.BLOODWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING), () -> Blocks.NETHERRACK));


    public static final DeferredBlock<Block> CHAIR = registerBlock("chair",
            () -> new ChairBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredBlock<Block> PEDESTAL = registerBlock("pedestal",
            () -> new PedestalBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredBlock<Block> GROWTH_CHAMBER = registerBlock("growth_chamber",
            () -> new GrowthChamberBlock(BlockBehaviour.Properties.of()));

    //my blocks

    //register blocks
    public static final DeferredBlock<Block> ZINC_ORE = registerBlock("zinc_ore",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(3.0F, 3.0F)
            )
    );
    public static final DeferredBlock<Block> ZINC_DEEPSLATE_ORE = registerBlock("zinc_deepslate_ore",
            () -> new DropExperienceBlock(UniformInt.of(3,6),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(4.0F, 3.0F)
            )
    );

    public static final DeferredBlock<Block> ZINC_END_ORE = registerBlock("zinc_end_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 9),
                    BlockBehaviour.Properties.of().strength(7f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ZINC_NETHER_ORE = registerBlock("zinc_nether_ore",
            () -> new DropExperienceBlock(UniformInt.of(1, 5),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WHEAT_INGOT_BLOCK = registerBlock("wheat_ingot_block",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BROWN)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(3.0F, 3.0F)
            )
    );public static final DeferredBlock<Block> ZINC_BLOCK = registerBlock("zinc_block",
            () -> new DropExperienceBlock(UniformInt.of(2,4),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops()
                            .randomTicks()
                            .strength(3.0F, 3.0F)
            )
    );
    public static final DeferredBlock<StairBlock> ZINC_STAIRS = registerBlock("zinc_stairs",
            () -> new StairBlock(ModBlocks.ZINC_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> ZINC_SLAB = registerBlock("zinc_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<PressurePlateBlock> ZINC_PRESSURE_PLATE = registerBlock("zinc_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<ButtonBlock> ZINC_BUTTON = registerBlock("zinc_button",
            () -> new ButtonBlock(BlockSetType.IRON, 40, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noCollission()));

    public static final DeferredBlock<FenceBlock> ZINC_FENCE = registerBlock("zinc_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FenceGateBlock> ZINC_FENCE_GATE = registerBlock("zinc_fence_gate",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<WallBlock> ZINC_WALL = registerBlock("zinc_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<DoorBlock> ZINC_DOOR = registerBlock("zinc_door",
            () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> ZINC_TRAPDOOR = registerBlock("zinc_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().noOcclusion()));

    //custom blocks
    //public static final DeferredBlock<Block> IRON_FURNACE = registerBlock("iron_furnace",
    //        () -> new IronFurnaceBlock());
    public static final DeferredBlock<Block> ZINC_CASING = registerBlock("zinc_casing",
            () -> new ZincCasingBlock());
    public static final DeferredBlock<Block> ANDESITE_CASING = registerBlock("andesite_casing",
            () -> new AndesiteCasingBlock());
    //public static final DeferredBlock<Block> LIQUIFIER = registerBlock("liquifier",
    //        () -> new LiquifierBlock(BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops()));
    //public static final DeferredBlock<Block> BIO_GENERATOR = registerBlock("bio_generator",
    //        () -> new BioGeneratorBlock(BlockBehaviour.Properties.of().strength(3.0f, 3.0f).requiresCorrectToolForDrops()));

    //public static final DeferredHolder<Block, LiquidBlock> BIO_FUEL_BLOCK = BLOCKS.register("biofuel_block",
    //        () -> new BioFuelBlock(ModFluids.BIOFUEL,
    //                BlockBehaviour.Properties.of()
    //                        .mapColor(MapColor.COLOR_GREEN)
    //                        .replaceable()
    //                        .noCollission()
    //                        .strength(100.0F)
    //                        .pushReaction(PushReaction.DESTROY)
    //                        .noLootTable()
    //                        .liquid()
    //                        .sound(SoundType.EMPTY)));

    // Energy System Blocks
    //public static final DeferredBlock<Block> ENERGY_CABLE = registerBlock("energy_cable",
            //() -> new EnergyCableBlock());

    //public static final DeferredBlock<Block> COAL_GENERATOR = registerBlock("coal_generator",
            //() -> new CoalGeneratorBlock());

    //public static final DeferredBlock<Block> ENERGY_BATTERY = registerBlock("energy_battery",
            //() -> new EnergyBatteryBlock());

    //Blockstates Example
    public static final DeferredBlock<Block> ZINC_LAMP = registerBlock("zinc_lamp",
            () -> new ZincLampBlock(BlockBehaviour.Properties.of().strength(2f)
                    .requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ZincLampBlock.CLICKED) ? 15 : 0)));


    public static final DeferredBlock<Block> CORN_CROP = BLOCKS.register("corn_crop",
            () -> new CornCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    public static final DeferredBlock<Block> STRAWBERRY_BUSH = BLOCKS.register("strawberry_bush",
            () -> new StrawberryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));

    public static final DeferredBlock<Block> WILDFLOWER_BUSH = registerBlock("wildflower_bush",
            () -> new WildFlowerBushBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .randomTicks()
                    .noCollission()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .strength(0.2F)
                    .pushReaction(PushReaction.DESTROY)));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
