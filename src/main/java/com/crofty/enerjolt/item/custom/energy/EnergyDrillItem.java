package com.crofty.enerjolt.item.custom.energy;

import com.crofty.enerjolt.energy.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState; /**
 * Energy-powered drill/pickaxe
 */
public class EnergyDrillItem extends EnergyToolItem {

    public EnergyDrillItem(Properties properties, EnergyTier tier) {
        super(properties, tier, calculateEnergyPerUse(tier));
    }

    private static int calculateEnergyPerUse(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 50;      // 50 EU per block
            case MEDIUM -> 80;   // 80 EU per block
            case HIGH -> 120;    // 120 EU per block
            case EXTREME -> 200; // 200 EU per block
            case INSANE -> 320;  // 320 EU per block
            case LUDICROUS -> 500; // 500 EU per block
            case ZENITH -> 800;  // 800 EU per block
            case ULTIMATE -> 1200; // 1200 EU per block
            case CREATIVE -> 0;  // Free
        };
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (level.isClientSide || energyPerUse == 0) return true;

        if (consumeEnergy(stack, energyPerUse)) {
            return true;
        }

        return false; // Not enough energy
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (getEnergyStored(stack) < energyPerUse) {
            return 1.0f; // Slow mining without energy
        }

        // Mining speed based on tier
        return switch (tier) {
            case LOW -> 6.0f;
            case MEDIUM -> 8.0f;
            case HIGH -> 12.0f;
            case EXTREME -> 16.0f;
            case INSANE -> 20.0f;
            case LUDICROUS -> 25.0f;
            case ZENITH -> 30.0f;
            case ULTIMATE -> 35.0f;
            case CREATIVE -> 100.0f;
        };
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // Check if we have enough energy
        return getEnergyStored(stack) >= energyPerUse;
    }
}
