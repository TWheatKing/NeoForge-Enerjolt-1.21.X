package com.crofty.enerjolt.effect;

import com.crofty.enerjolt.Enerjolt;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Enerjolt.MOD_ID);

    public static final Holder<MobEffect> SLIMEY_EFFECT = MOB_EFFECTS.register("slimey",
            () -> new SlimeyEffect(MobEffectCategory.NEUTRAL, 0x36ebab)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "slimey"), -0.25f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final Holder<MobEffect> ATTRACTION_EFFECT = MOB_EFFECTS.register("attraction",
            () -> new AttractionEffect(MobEffectCategory.NEUTRAL, 0x90EE90)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            ResourceLocation.fromNamespaceAndPath(Enerjolt.MOD_ID, "attraction"), -0.25f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
