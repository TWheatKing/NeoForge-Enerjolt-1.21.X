package com.crofty.enerjolt.energy;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Energy Tier System for Enerjolt
 * Defines different energy tiers with their characteristics and compatibility
 */
public enum EnergyTier {
    // Basic Tiers
    LOW("Low Voltage", "LV", 32, 128, 1.5f, 0xFF00AA00, ChatFormatting.GREEN),
    MEDIUM("Medium Voltage", "MV", 128, 512, 1.2f, 0xFF0066CC, ChatFormatting.BLUE),
    HIGH("High Voltage", "HV", 512, 2048, 1.0f, 0xFFCC6600, ChatFormatting.GOLD),

    // Advanced Tiers
    EXTREME("Extreme Voltage", "EV", 2048, 8192, 0.8f, 0xFFCC0066, ChatFormatting.GOLD),
    INSANE("Insane Voltage", "IV", 8192, 32768, 0.6f, 0xFF6600CC, ChatFormatting.DARK_PURPLE),
    LUDICROUS("Ludicrous Voltage", "LuV", 32768, 131072, 0.4f, 0xFFFF0066, ChatFormatting.RED),

    // Ultimate Tiers
    ZENITH("Zenith Voltage", "ZV", 131072, 524288, 0.2f, 0xFF000000, ChatFormatting.DARK_GRAY),
    ULTIMATE("Ultimate Voltage", "UV", 524288, Integer.MAX_VALUE, 0.1f, 0xFFFFFFFF, ChatFormatting.WHITE),

    // Special Tiers
    CREATIVE("Creative", "∞", Integer.MAX_VALUE, Integer.MAX_VALUE, 0.0f, 0xFFFF00FF, ChatFormatting.LIGHT_PURPLE);

    // Codec support for data components
    public static final Codec<EnergyTier> CODEC = Codec.STRING.xmap(EnergyTier::valueOf, EnergyTier::name);
    public static final StreamCodec<?, EnergyTier> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(EnergyTier::valueOf, EnergyTier::name);

    private final String displayName;
    private final String shortName;
    private final int voltage;
    private final int maxCapacity;
    private final float lossMultiplier;
    private final int color;
    private final ChatFormatting textColor;

    EnergyTier(String displayName, String shortName, int voltage, int maxCapacity,
               float lossMultiplier, int color, ChatFormatting textColor) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.voltage = voltage;
        this.maxCapacity = maxCapacity;
        this.lossMultiplier = lossMultiplier;
        this.color = color;
        this.textColor = textColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public float getLossMultiplier() {
        return lossMultiplier;
    }

    public int getColor() {
        return color;
    }

    public ChatFormatting getTextColor() {
        return textColor;
    }

    /**
     * Get the next tier up, or null if this is the highest
     */
    public EnergyTier getNextTier() {
        EnergyTier[] tiers = values();
        for (int i = 0; i < tiers.length - 1; i++) {
            if (tiers[i] == this) {
                return tiers[i + 1];
            }
        }
        return null; // Already at highest tier
    }

    /**
     * Get the previous tier down, or null if this is the lowest
     */
    public EnergyTier getPreviousTier() {
        EnergyTier[] tiers = values();
        for (int i = 1; i < tiers.length; i++) {
            if (tiers[i] == this) {
                return tiers[i - 1];
            }
        }
        return null; // Already at lowest tier
    }

    /**
     * Check if this tier is compatible with another tier for energy transfer
     */
    public boolean isCompatibleWith(EnergyTier other) {
        if (this == CREATIVE || other == CREATIVE) return true;

        // Allow transfers within 1 tier difference
        int thisTierIndex = this.ordinal();
        int otherTierIndex = other.ordinal();
        return Math.abs(thisTierIndex - otherTierIndex) <= 1;
    }

    /**
     * Calculate the efficiency when transferring to another tier
     */
    public float getTransferEfficiency(EnergyTier targetTier) {
        if (this == targetTier) return 1.0f;
        if (this == CREATIVE || targetTier == CREATIVE) return 1.0f;

        int tierDifference = Math.abs(this.ordinal() - targetTier.ordinal());

        // Efficiency decreases with tier difference
        switch (tierDifference) {
            case 0: return 1.0f;
            case 1: return 0.9f;  // 90% efficiency
            case 2: return 0.75f; // 75% efficiency
            case 3: return 0.5f;  // 50% efficiency
            default: return 0.25f; // 25% efficiency for large differences
        }
    }

    /**
     * Get maximum safe transfer rate between tiers
     */
    public int getSafeTransferRate(EnergyTier targetTier) {
        if (!isCompatibleWith(targetTier)) return 0;
        return Math.min(this.voltage, targetTier.voltage);
    }

    /**
     * Create a colored display component for this tier
     */
    public MutableComponent getDisplayComponent() {
        return Component.literal(shortName).withStyle(textColor);
    }

    public MutableComponent getFullDisplayComponent() {
        return Component.literal(displayName + " (" + shortName + ")").withStyle(textColor);
    }

    /**
     * Get tier by voltage level
     */
    public static EnergyTier getTierByVoltage(int voltage) {
        for (EnergyTier tier : values()) {
            if (tier == CREATIVE) continue;
            if (voltage <= tier.voltage) {
                return tier;
            }
        }
        return ULTIMATE; // Highest non-creative tier
    }

    /**
     * Format energy amount with proper units and tier coloring
     */
    public static MutableComponent formatEnergy(int energy, EnergyTier tier) {
        String formatted = formatEnergyAmount(energy);
        return Component.literal(formatted + " EU").withStyle(tier.textColor);
    }

    private static String formatEnergyAmount(int energy) {
        if (energy >= 1_000_000_000) {
            return String.format("%.2fG", energy / 1_000_000_000.0);
        } else if (energy >= 1_000_000) {
            return String.format("%.2fM", energy / 1_000_000.0);
        } else if (energy >= 1_000) {
            return String.format("%.2fk", energy / 1_000.0);
        } else {
            return String.valueOf(energy);
        }
    }

    /**
     * Check if voltage would cause an explosion/damage
     */
    public boolean isVoltageDangerous(EnergyTier inputTier) {
        if (this == CREATIVE || inputTier == CREATIVE) return false;

        // Dangerous if input is more than 2 tiers higher
        return (inputTier.ordinal() - this.ordinal()) > 2;
    }

    /**
     * Get explosion power for voltage overload
     */
    public float getExplosionPower(EnergyTier inputTier) {
        if (!isVoltageDangerous(inputTier)) return 0.0f;

        int tierDifference = inputTier.ordinal() - this.ordinal();
        return Math.min(10.0f, tierDifference * 2.0f);
    }
}