package com.crofty.enerjolt.item.custom.energy;

import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.ModEnergyComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List; /**
 * Portable energy battery item
 */
public class EnergyBatteryItem extends EnergyToolItem {

    public EnergyBatteryItem(Properties properties, EnergyTier tier) {
        super(properties, tier, 0); // Batteries don't consume energy for use
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("tooltip.enerjolt.portable_battery"));
        tooltipComponents.add(Component.translatable("tooltip.enerjolt.max_io", tier.getVoltage()));
    }

    /**
     * Battery can charge other items in inventory
     */
    public void chargeAdjacentItems(ItemStack battery, Player player) {
        var batteryData = battery.get(ModEnergyComponents.ENERGY_DATA.get());
        if (batteryData == null || batteryData.energy() == 0) return;

        int transferRate = tier.getVoltage() / 10; // 10% of tier voltage per tick

        for (ItemStack stack : player.getInventory().items) {
            if (stack == battery || stack.isEmpty()) continue;

            var targetData = stack.get(ModEnergyComponents.ENERGY_DATA.get());
            if (targetData == null || targetData.isFull()) continue;

            // Check tier compatibility
            if (!tier.isCompatibleWith(targetData.tier())) continue;

            int canTransfer = Math.min(transferRate, batteryData.energy());
            int canReceive = targetData.capacity() - targetData.energy();
            int actualTransfer = Math.min(canTransfer, canReceive);

            if (actualTransfer > 0) {
                // Transfer energy
                battery.set(ModEnergyComponents.ENERGY_DATA.get(),
                        batteryData.withEnergy(batteryData.energy() - actualTransfer));
                stack.set(ModEnergyComponents.ENERGY_DATA.get(),
                        targetData.withEnergy(targetData.energy() + actualTransfer));
                break; // Only charge one item per tick
            }
        }
    }
}
