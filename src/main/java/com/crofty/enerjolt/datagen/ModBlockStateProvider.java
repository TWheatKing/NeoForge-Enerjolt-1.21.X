package com.crofty.enerjolt.datagen;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.ModBlocks;
import com.crofty.enerjolt.block.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Enerjolt.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.BISMUTH_BLOCK);

        blockWithItem(ModBlocks.BISMUTH_ORE);
        blockWithItem(ModBlocks.BISMUTH_DEEPSLATE_ORE);

        blockWithItem(ModBlocks.MAGIC_BLOCK);

        stairsBlock(ModBlocks.BISMUTH_STAIRS.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));
        slabBlock(ModBlocks.BISMUTH_SLAB.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));

        buttonBlock(ModBlocks.BISMUTH_BUTTON.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));
        pressurePlateBlock(ModBlocks.BISMUTH_PRESSURE_PLATE.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));

        fenceBlock(ModBlocks.BISMUTH_FENCE.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));
        fenceGateBlock(ModBlocks.BISMUTH_FENCE_GATE.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));
        wallBlock(ModBlocks.BISMUTH_WALL.get(), blockTexture(ModBlocks.BISMUTH_BLOCK.get()));

        doorBlockWithRenderType(ModBlocks.BISMUTH_DOOR.get(), modLoc("block/bismuth_door_bottom"), modLoc("block/bismuth_door_top"), "cutout");
        trapdoorBlockWithRenderType(ModBlocks.BISMUTH_TRAPDOOR.get(), modLoc("block/bismuth_trapdoor"), true, "cutout");

        blockItem(ModBlocks.BISMUTH_STAIRS);
        blockItem(ModBlocks.BISMUTH_SLAB);
        blockItem(ModBlocks.BISMUTH_PRESSURE_PLATE);
        blockItem(ModBlocks.BISMUTH_FENCE_GATE);
        blockItem(ModBlocks.BISMUTH_TRAPDOOR, "_bottom");

        makeCrop(((CropBlock) ModBlocks.RADISH_CROP.get()), "radish_crop_stage", "radish_crop_stage");
        makeBush(((SweetBerryBushBlock) ModBlocks.GOJI_BERRY_BUSH.get()), "goji_berry_bush_stage", "goji_berry_bush_stage");

        blockWithItem(ModBlocks.BISMUTH_END_ORE);
        blockWithItem(ModBlocks.BISMUTH_NETHER_ORE);

        logBlock(((RotatedPillarBlock) ModBlocks.BLOODWOOD_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.BLOODWOOD_WOOD.get()), blockTexture(ModBlocks.BLOODWOOD_LOG.get()), blockTexture(ModBlocks.BLOODWOOD_LOG.get()));
        logBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_BLOODWOOD_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_BLOODWOOD_WOOD.get()), blockTexture(ModBlocks.STRIPPED_BLOODWOOD_LOG.get()), blockTexture(ModBlocks.STRIPPED_BLOODWOOD_LOG.get()));

        blockItem(ModBlocks.BLOODWOOD_LOG);
        blockItem(ModBlocks.BLOODWOOD_WOOD);
        blockItem(ModBlocks.STRIPPED_BLOODWOOD_LOG);
        blockItem(ModBlocks.STRIPPED_BLOODWOOD_WOOD);

        blockWithItem(ModBlocks.BLOODWOOD_PLANKS);

        leavesBlock(ModBlocks.BLOODWOOD_LEAVES);
        saplingBlock(ModBlocks.BLOODWOOD_SAPLING);

        blockWithItem(ModBlocks.GROWTH_CHAMBER);

        //my stuff

        blockWithItem(ModBlocks.ZINC_ORE);
        blockWithItem(ModBlocks.ZINC_DEEPSLATE_ORE);
        blockWithItem(ModBlocks.ZINC_END_ORE);
        blockWithItem(ModBlocks.ZINC_NETHER_ORE);
        blockWithItem(ModBlocks.ZINC_BLOCK);
        blockWithItem(ModBlocks.ZINC_CASING);
        blockWithItem(ModBlocks.ANDESITE_CASING);
        blockWithItem(ModBlocks.WHEAT_INGOT_BLOCK);

        stairsBlock(ModBlocks.ZINC_STAIRS.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        slabBlock(ModBlocks.ZINC_SLAB.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()), blockTexture(ModBlocks.ZINC_BLOCK.get()));

        buttonBlock(ModBlocks.ZINC_BUTTON.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        pressurePlateBlock(ModBlocks.ZINC_PRESSURE_PLATE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        fenceGateBlock(ModBlocks.ZINC_FENCE_GATE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        fenceBlock(ModBlocks.ZINC_FENCE.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));
        wallBlock(ModBlocks.ZINC_WALL.get(), blockTexture(ModBlocks.ZINC_BLOCK.get()));

        doorBlockWithRenderType(ModBlocks.ZINC_DOOR.get(), modLoc("block/zinc_door_bottom"), modLoc("block/zinc_door_top"), "cutout");
        trapdoorBlockWithRenderType(ModBlocks.ZINC_TRAPDOOR.get(), modLoc("block/zinc_trapdoor"), true, "cutout");

        blockItem(ModBlocks.ZINC_STAIRS);
        blockItem(ModBlocks.ZINC_SLAB);
        blockItem(ModBlocks.ZINC_PRESSURE_PLATE);
        blockItem(ModBlocks.ZINC_FENCE_GATE);
        blockItem(ModBlocks.ZINC_TRAPDOOR, "_bottom");

        customLamp();

        makeCrop(((CropBlock) ModBlocks.CORN_CROP.get()), "corn_crop_stage", "corn_crop_stage");
        makeBush(((SweetBerryBushBlock) ModBlocks.STRAWBERRY_BUSH.get()), "strawberry_bush_stage", "strawberry_bush_stage");
        // Add this to your registerStatesAndModels() method
        makeWildflowerBush();
    }

    private void makeWildflowerBush() {
        Function<BlockState, ConfiguredModel[]> function = state -> wildflowerBushStates(state);
        getVariantBuilder(ModBlocks.WILDFLOWER_BUSH.get()).forAllStates(function);
    }

    private ConfiguredModel[] wildflowerBushStates(BlockState state) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        int age = state.getValue(WildFlowerBushBlock.AGE);

        models[0] = new ConfiguredModel(models().cross("wildflower_bush_stage" + age,
                ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID,
                        "block/wildflower_bush_stage" + age)).renderType("cutout"));

        return models;
    }

    public void makeBush(SweetBerryBushBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().cross(modelName + state.getValue(StrawberryBushBlock.AGE),
                ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "block/" + textureName + state.getValue(StrawberryBushBlock.AGE))).renderType("cutout"));

        return models;
    }

    public void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];

        // Find the age property from the block state's properties
        IntegerProperty ageProperty = null;
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty && property.getName().equals("age")) {
                ageProperty = (IntegerProperty) property;
                break;
            }
        }

        // Fallback to standard CropBlock.AGE if no age property found
        if (ageProperty == null) {
            ageProperty = CropBlock.AGE;
        }

        int age = state.getValue(ageProperty);

        models[0] = new ConfiguredModel(models().crop(modelName + age,
                ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "block/" + textureName + age)).renderType("cutout"));

        return models;
    }

    private void customLamp() {
        getVariantBuilder(ModBlocks.ZINC_LAMP.get()).forAllStates(state -> {
            if(state.getValue(ZincLampBlock.CLICKED)) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("zinc_lamp_on",
                        ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "block/" + "zinc_lamp_on")))};
            } else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("zinc_lamp_off",
                        ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "block/" + "zinc_lamp_off")))};
            }
        });

        simpleBlockItem(ModBlocks.ZINC_LAMP.get(), models().cubeAll("zinc_lamp_on",
                ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "block/" + "zinc_lamp_on")));
    }

    // Helper method to get block name
    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }


    private void saplingBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("enerjolt:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("enerjolt:block/" + deferredBlock.getId().getPath() + appendix));
    }
}
