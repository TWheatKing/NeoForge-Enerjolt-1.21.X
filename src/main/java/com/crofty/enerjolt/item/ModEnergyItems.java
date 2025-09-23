package com.crofty.enerjolt.item;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.ModEnergyComponents;
import com.crofty.enerjolt.item.custom.energy.EnergyBatteryItem;
import com.crofty.enerjolt.item.custom.energy.EnergyDrillItem;
import com.crofty.enerjolt.item.custom.energy.EnergyToolItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registration for all energy-related items
 */
public class ModEnergyItems {
    public static final DeferredRegister.Items ENERGY_ITEMS = DeferredRegister.createItems(Enerjolt.MOD_ID);

    // Energy Batteries - All Tiers
    public static final DeferredItem<Item> LV_BATTERY = ENERGY_ITEMS.register("lv_battery",
            () -> createBattery(EnergyTier.LOW));
    public static final DeferredItem<Item> MV_BATTERY = ENERGY_ITEMS.register("mv_battery",
            () -> createBattery(EnergyTier.MEDIUM));
    public static final DeferredItem<Item> HV_BATTERY = ENERGY_ITEMS.register("hv_battery",
            () -> createBattery(EnergyTier.HIGH));
    public static final DeferredItem<Item> EV_BATTERY = ENERGY_ITEMS.register("ev_battery",
            () -> createBattery(EnergyTier.EXTREME));
    public static final DeferredItem<Item> IV_BATTERY = ENERGY_ITEMS.register("iv_battery",
            () -> createBattery(EnergyTier.INSANE));
    public static final DeferredItem<Item> LUV_BATTERY = ENERGY_ITEMS.register("luv_battery",
            () -> createBattery(EnergyTier.LUDICROUS));
    public static final DeferredItem<Item> ZV_BATTERY = ENERGY_ITEMS.register("zv_battery",
            () -> createBattery(EnergyTier.ZENITH));
    public static final DeferredItem<Item> UV_BATTERY = ENERGY_ITEMS.register("uv_battery",
            () -> createBattery(EnergyTier.ULTIMATE));
    public static final DeferredItem<Item> CREATIVE_BATTERY = ENERGY_ITEMS.register("creative_battery",
            () -> createBattery(EnergyTier.CREATIVE));

    // Energy Drills - All Tiers
    public static final DeferredItem<Item> LV_DRILL = ENERGY_ITEMS.register("lv_drill",
            () -> createDrill(EnergyTier.LOW));
    public static final DeferredItem<Item> MV_DRILL = ENERGY_ITEMS.register("mv_drill",
            () -> createDrill(EnergyTier.MEDIUM));
    public static final DeferredItem<Item> HV_DRILL = ENERGY_ITEMS.register("hv_drill",
            () -> createDrill(EnergyTier.HIGH));
    public static final DeferredItem<Item> EV_DRILL = ENERGY_ITEMS.register("ev_drill",
            () -> createDrill(EnergyTier.EXTREME));
    public static final DeferredItem<Item> IV_DRILL = ENERGY_ITEMS.register("iv_drill",
            () -> createDrill(EnergyTier.INSANE));
    public static final DeferredItem<Item> LUV_DRILL = ENERGY_ITEMS.register("luv_drill",
            () -> createDrill(EnergyTier.LUDICROUS));
    public static final DeferredItem<Item> ZV_DRILL = ENERGY_ITEMS.register("zv_drill",
            () -> createDrill(EnergyTier.ZENITH));
    public static final DeferredItem<Item> UV_DRILL = ENERGY_ITEMS.register("uv_drill",
            () -> createDrill(EnergyTier.ULTIMATE));
    public static final DeferredItem<Item> CREATIVE_DRILL = ENERGY_ITEMS.register("creative_drill",
            () -> createDrill(EnergyTier.CREATIVE));

    // Energy Multitool - Advanced tools that can switch modes
    public static final DeferredItem<Item> ENERGY_MULTITOOL = ENERGY_ITEMS.register("energy_multitool",
            () -> new EnergyMultitoolItem(new Item.Properties(), EnergyTier.HIGH));

    // Debug/Creative Items
    public static final DeferredItem<Item> ENERGY_SCANNER = ENERGY_ITEMS.register("energy_scanner",
            () -> new EnergyScannerItem(new Item.Properties()));
    public static final DeferredItem<Item> ENERGY_DEBUGGER = ENERGY_ITEMS.register("energy_debugger",
            () -> new EnergyDebuggerItem(new Item.Properties()));

    private static EnergyBatteryItem createBattery(EnergyTier tier) {
        return new EnergyBatteryItem(new Item.Properties(), tier) {
            @Override
            public void onCraftedBy(net.minecraft.world.item.ItemStack stack,
                                    net.minecraft.world.level.Level level,
                                    net.minecraft.world.entity.player.Player player) {
                // Initialize with energy data
                initializeEnergyData(stack, tier);
            }
        };
    }

    private static EnergyDrillItem createDrill(EnergyTier tier) {
        return new EnergyDrillItem(new Item.Properties(), tier) {
            @Override
            public void onCraftedBy(net.minecraft.world.item.ItemStack stack,
                                    net.minecraft.world.level.Level level,
                                    net.minecraft.world.entity.player.Player player) {
                // Initialize with energy data
                initializeEnergyData(stack, tier);
            }
        };
    }

    private static void initializeEnergyData(net.minecraft.world.item.ItemStack stack, EnergyTier tier) {
        int capacity = calculateItemCapacity(tier);
        var energyData = new ModEnergyComponents.EnergyData(0, capacity, tier, 1.0f);
        var energyConfig = ModEnergyComponents.EnergyConfig.createDefault(tier);

        stack.set(ModEnergyComponents.ENERGY_DATA.get(), energyData);
        stack.set(ModEnergyComponents.ENERGY_CONFIG.get(), energyConfig);
        stack.set(ModEnergyComponents.ENERGY_TIER.get(), tier);
    }

    private static int calculateItemCapacity(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 10000;
            case MEDIUM -> 40000;
            case HIGH -> 160000;
            case EXTREME -> 640000;
            case INSANE -> 2560000;
            case LUDICROUS -> 10240000;
            case ZENITH -> 40960000;
            case ULTIMATE -> 163840000;
            case CREATIVE -> Integer.MAX_VALUE;
        };
    }

    public static void register(IEventBus eventBus) {
        ENERGY_ITEMS.register(eventBus);
    }
}

// Additional Energy Item Classes

/**
 * Energy Multitool - Can switch between pickaxe, axe, shovel modes
 */
class EnergyMultitoolItem extends EnergyToolItem {
    public EnergyMultitoolItem(Item.Properties properties, EnergyTier tier) {
        super(properties, tier, calculateEnergyPerUse(tier));
    }

    private static int calculateEnergyPerUse(EnergyTier tier) {
        return tier.getVoltage() / 10; // Efficient multitool
    }

    // TODO: Implement mode switching and tool functionality
}

/**
 * Energy Scanner - Shows energy information about blocks and items
 */
class EnergyScannerItem extends Item {
    public EnergyScannerItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    // TODO: Implement energy scanning functionality
    // - Right-click blocks to show energy info
    // - Display energy network information
    // - Show cable connections and energy flow
}

/**
 * Energy Debugger - Creative-only debugging tool
 */
class EnergyDebuggerItem extends Item {
    public EnergyDebuggerItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    // TODO: Implement debugging features
    // - Set energy levels in machines
    // - Force energy transfers
    // - Display detailed energy statistics
    // - Simulate energy network load
}

// FILE: Integration with existing Growth Chamber
// Add this to your existing GrowthChamberBlockEntity class:

/**
 * Enhanced Growth Chamber with Energy Support
 * Add this code to your existing GrowthChamberBlockEntity
 */
/*
// Add these fields to existing GrowthChamberBlockEntity:
private EnerjoltEnergyStorage energyStorage;
private boolean useEnergy = false;

// Add this to constructor:
this.energyStorage = new EnerjoltEnergyStorage(
    10000, // 10k EU capacity
    EnergyTier.LOW.getVoltage(), // Can receive LV
    0, // Cannot output
    EnergyTier.LOW,
    true, false, false
);

// Modify the tick method to use energy:
public void tick(Level level, BlockPos blockPos, BlockState blockState) {
    if(hasRecipe()) {
        // Use energy if available, otherwise use time-based processing
        boolean hasEnergy = energyStorage.getEnergyStored() >= 20;

        if (hasEnergy || !useEnergy) {
            if (hasEnergy) {
                energyStorage.extractEnergy(20, false); // Consume 20 EU/t
                increaseCraftingProgress(2); // 2x speed with energy
            } else {
                increaseCraftingProgress(); // Normal speed without energy
            }

            setChanged(level, blockPos, blockState);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        }
    } else {
        resetProgress();
    }
}

// Add energy capability:
public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
    return new EnergyCapabilityProvider.DirectionalEnergyStorage(
        energyStorage, direction, true, false);
}
*/