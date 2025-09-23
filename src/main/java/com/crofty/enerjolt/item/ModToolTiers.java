package com.crofty.enerjolt.item;

import com.crofty.enerjolt.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier ZINC = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_ZINC_TOOL,
            1400, 4f, 3f, 28, () -> Ingredient.of(ModItems.ZINC_INGOT));

}
