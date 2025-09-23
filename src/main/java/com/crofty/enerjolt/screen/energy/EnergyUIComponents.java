package com.crofty.enerjolt.screen.energy;

import com.crofty.enerjolt.energy.EnergyTier; /**
 * Energy UI Components
 * Utility class for common energy GUI elements
 */
public class EnergyUIComponents {

    /**
     * Render energy bar in GUI
     */
    public static void renderEnergyBar(net.minecraft.client.gui.GuiGraphics graphics,
                                       int x, int y, int width, int height,
                                       int energy, int maxEnergy, EnergyTier tier) {
        if (maxEnergy == 0) return;

        // Background
        graphics.fill(x, y, x + width, y + height, 0xFF000000);

        // Energy fill
        int fillHeight = (int) ((double) energy / maxEnergy * height);
        int color = tier.getColor();
        graphics.fill(x + 1, y + height - fillHeight, x + width - 1, y + height, color);

        // Border
        graphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF); // Right
        graphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF); // Bottom
    }

    /**
     * Render progress arrow
     */
    public static void renderProgressArrow(net.minecraft.client.gui.GuiGraphics graphics,
                                           int x, int y, int progress) {
        // Arrow background (24x17 texture)
        graphics.blit(net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
                x, y, 176, 14, 24, 17);

        // Progress fill
        if (progress > 0) {
            graphics.blit(net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
                    x, y, 176, 0, progress, 17);
        }
    }

    /**
     * Format energy amount for display
     */
    public static String formatEnergy(int energy) {
        if (energy >= 1_000_000_000) {
            return String.format("%.1fG", energy / 1_000_000_000.0);
        } else if (energy >= 1_000_000) {
            return String.format("%.1fM", energy / 1_000_000.0);
        } else if (energy >= 1_000) {
            return String.format("%.1fk", energy / 1_000.0);
        } else {
            return String.valueOf(energy);
        }
    }
}
