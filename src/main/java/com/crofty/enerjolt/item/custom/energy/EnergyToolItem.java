package com.crofty.enerjolt.item.custom.energy;

import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.ModEnergyComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.ModEnergyComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List; /**
 * Energy-powered tool base class
 */
public abstract class EnergyToolItem extends Item {
    protected final EnergyTier tier;
    protected final int energyPerUse;
    protected final int capacity;

    public EnergyToolItem(Properties properties, EnergyTier tier, int energyPerUse) {
        super(properties.stacksTo(1));
        this.tier = tier;
        this.energyPerUse = energyPerUse;
        this.capacity = calculateCapacity(tier);
    }

    private int calculateCapacity(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 10000;     // 10k EU
            case MEDIUM -> 40000;  // 40k EU
            case HIGH -> 160000;   // 160k EU
            case EXTREME -> 640000; // 640k EU
            case INSANE -> 2560000; // 2.5M EU
            case LUDICROUS -> 10240000; // 10M EU
            case ZENITH -> 40960000; // 40M EU
            case ULTIMATE -> 163840000; // 163M EU
            case CREATIVE -> Integer.MAX_VALUE; // Infinite
        };
    }

    protected boolean consumeEnergy(ItemStack stack, int amount) {
        var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
        if (energyData == null) {
            // Initialize energy data
            energyData = new ModEnergyComponents.EnergyData(capacity, capacity, tier, 1.0f);
            stack.set(ModEnergyComponents.ENERGY_DATA.get(), energyData);
        }

        if (energyData.energy() >= amount) {
            stack.set(ModEnergyComponents.ENERGY_DATA.get(),
                    energyData.withEnergy(energyData.energy() - amount));
            return true;
        }
        return false;
    }

    protected int getEnergyStored(ItemStack stack) {
        var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
        return energyData != null ? energyData.energy() : 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true; // Always show energy bar
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
        if (energyData == null) return 0;
        return Math.round(13.0f * energyData.getStoredPercentage());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return tier.getColor();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        var energyData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
        if (energyData != null) {
            tooltipComponents.add(EnergyTier.formatEnergy(energyData.energy(), tier)
                    .append(" / ")
                    .append(EnergyTier.formatEnergy(energyData.capacity(), tier)));

            tooltipComponents.add(Component.translatable("tooltip.enerjolt.energy_tier")
                    .append(": ").append(tier.getDisplayComponent()));

            if (energyPerUse > 0) {
                tooltipComponents.add(Component.translatable("tooltip.enerjolt.energy_per_use")
                        .append(": ").append(EnergyTier.formatEnergy(energyPerUse, tier)));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
