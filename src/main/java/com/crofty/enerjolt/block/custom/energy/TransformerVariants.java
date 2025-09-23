package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.energy.EnergyTier;

import java.util.Properties;

/**
 * Utility class for creating transformer variants
 */
public class TransformerVariants {

    /**
     * Create a step-up transformer (low to high voltage)
     */
    public static EnergyTransformerBlock createStepUp(Properties properties, EnergyTier from, EnergyTier to) {
        if (to.ordinal() <= from.ordinal()) {
            throw new IllegalArgumentException("Step-up transformer requires higher output tier");
        }
        return new EnergyTransformerBlock(properties, from, to);
    }

    /**
     * Create a step-down transformer (high to low voltage)
     */
    public static EnergyTransformerBlock createStepDown(Properties properties, EnergyTier from, EnergyTier to) {
        if (to.ordinal() >= from.ordinal()) {
            throw new IllegalArgumentException("Step-down transformer requires lower output tier");
        }
        return new EnergyTransformerBlock(properties, from, to);
    }

    /**
     * Create all standard transformer combinations
     */
    public static void registerStandardTransformers(/* Your registration system */) {
        // Low Voltage transformers
        // LV -> MV Step-up
        // MV -> LV Step-down

        // Medium Voltage transformers
        // MV -> HV Step-up
        // HV -> MV Step-down

        // High Voltage transformers
        // HV -> EV Step-up
        // EV -> HV Step-down

        // And so on for all tiers...
    }
}
