package com.crofty.enerjolt.datagen;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.ModBlocks;
import com.crofty.enerjolt.item.ModItems;
import com.crofty.enerjolt.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Enerjolt.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.ZINC_INGOT.get())
                .add(ModItems.RAW_ZINC.get())
                .add(Items.COAL)
                .add(Items.STICK)
                .add(Items.COMPASS);

        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.BLOODWOOD_LOG.get().asItem())
                .add(ModBlocks.BLOODWOOD_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_BLOODWOOD_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_BLOODWOOD_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.BLOODWOOD_PLANKS.asItem());

        tag(ItemTags.SWORDS)
                .add(ModItems.ZINC_SWORD.get());
        tag(ItemTags.PICKAXES)
                .add(ModItems.ZINC_PICKAXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModItems.ZINC_SHOVEL.get());
        tag(ItemTags.AXES)
                .add(ModItems.ZINC_AXE.get());
        tag(ItemTags.HOES)
                .add(ModItems.ZINC_HOE.get());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.ZINC_HELMET.get())
                .add(ModItems.ZINC_CHESTPLATE.get())
                .add(ModItems.ZINC_LEGGINGS.get())
                .add(ModItems.ZINC_BOOTS.get());

        this.tag(ItemTags.TRIM_MATERIALS)
                .add(ModItems.ZINC_INGOT.get());

        this.tag(ItemTags.TRIM_TEMPLATES)
                .add(ModItems.ZINC_SMITHING_TEMPLATE.get());

    }
}
