package com.crofty.enerjolt.screen.energy;

import com.crofty.enerjolt.Enerjolt;
import com.crofty.enerjolt.block.entity.energy.EnergyGeneratorBlockEntity;
import com.crofty.enerjolt.block.entity.energy.EnergyStorageBlockEntity;
import com.crofty.enerjolt.block.entity.energy.EnergyTransformerBlockEntity;
import com.crofty.enerjolt.block.entity.energy.ElectricSmelterBlockEntity;
import com.crofty.enerjolt.energy.EnergyTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Menu types registration for energy machines
 */
public class ModEnergyMenuTypes {
    public static final DeferredRegister<MenuType<?>> ENERGY_MENUS =
            DeferredRegister.create(Registries.MENU, Enerjolt.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyGeneratorMenu>> ENERGY_GENERATOR_MENU =
            registerMenuType("energy_generator_menu", EnergyGeneratorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyStorageMenu>> ENERGY_STORAGE_MENU =
            registerMenuType("energy_storage_menu", EnergyStorageMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EnergyTransformerMenu>> ENERGY_TRANSFORMER_MENU =
            registerMenuType("energy_transformer_menu", EnergyTransformerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ElectricSmelterMenu>> ELECTRIC_SMELTER_MENU =
            registerMenuType("electric_smelter_menu", ElectricSmelterMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return ENERGY_MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        ENERGY_MENUS.register(eventBus);
    }
}

/**
 * Energy Generator Menu
 */
class EnergyGeneratorMenu extends AbstractContainerMenu {
    public final EnergyGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public EnergyGeneratorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public EnergyGeneratorMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModEnergyMenuTypes.ENERGY_GENERATOR_MENU.get(), containerId);
        this.blockEntity = (EnergyGeneratorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Fuel slot (if generator uses items as fuel)
        // this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 80, 35));

        addDataSlots(data);
    }

    public boolean isGenerating() {
        return data.get(0) > 0; // Burn time
    }

    public int getBurnProgress() {
        int burnTime = data.get(0);
        int maxBurnTime = data.get(1);
        return maxBurnTime != 0 ? (burnTime * 24) / maxBurnTime : 0;
    }

    public int getEnergyProgress() {
        int energy = data.get(2);
        int maxEnergy = data.get(3);
        return maxEnergy != 0 ? (energy * 52) / maxEnergy : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // Implement based on your slot layout
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

/**
 * Energy Storage Menu
 */
class EnergyStorageMenu extends AbstractContainerMenu {
    public final EnergyStorageBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public EnergyStorageMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2));
    }

    public EnergyStorageMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModEnergyMenuTypes.ENERGY_STORAGE_MENU.get(), containerId);
        this.blockEntity = (EnergyStorageBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getMaxEnergyStored() {
        return data.get(1);
    }

    public int getEnergyBarHeight() {
        int maxEnergy = getMaxEnergyStored();
        return maxEnergy != 0 ? (getEnergyStored() * 52) / maxEnergy : 0;
    }

    public EnergyTier getTier() {
        return blockEntity.getTier();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

/**
 * Energy Transformer Menu
 */
class EnergyTransformerMenu extends AbstractContainerMenu {
    public final EnergyTransformerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public EnergyTransformerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(6));
    }

    public EnergyTransformerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModEnergyMenuTypes.ENERGY_TRANSFORMER_MENU.get(), containerId);
        this.blockEntity = (EnergyTransformerBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    public int getInputEnergyStored() {
        return data.get(0);
    }

    public int getInputMaxEnergy() {
        return data.get(1);
    }

    public int getOutputEnergyStored() {
        return data.get(2);
    }

    public int getOutputMaxEnergy() {
        return data.get(3);
    }

    public int getOverloadProgress() {
        return data.get(4);
    }

    public int getCooldownProgress() {
        return data.get(5);
    }

    public EnergyTier getInputTier() {
        return blockEntity.getInputTier();
    }

    public EnergyTier getOutputTier() {
        return blockEntity.getOutputTier();
    }

    public boolean isStepUp() {
        return blockEntity.isStepUp();
    }

    public float getEfficiency() {
        return blockEntity.getConversionEfficiency();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

/**
 * Electric Smelter Menu
 */
class ElectricSmelterMenu extends AbstractContainerMenu {
    public final ElectricSmelterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ElectricSmelterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(4));
    }

    public ElectricSmelterMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModEnergyMenuTypes.ELECTRIC_SMELTER_MENU.get(), containerId);
        this.blockEntity = (ElectricSmelterBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Input slot
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 56, 35));

        // Output slot
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output only
            }
        });

        // Upgrade slot
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 56, 53));

        addDataSlots(data);
    }

    public boolean isProcessing() {
        return data.get(0) > 0;
    }

    public int getProcessProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        return maxProgress != 0 ? (progress * 24) / maxProgress : 0;
    }

    public int getEnergyProgress() {
        int energy = data.get(2);
        int maxEnergy = data.get(3);
        return maxEnergy != 0 ? (energy * 52) / maxEnergy : 0;
    }

    public EnergyTier getTier() {
        return blockEntity.getTier();
    }

    public int getSpeedLevel() {
        return blockEntity.getSpeedLevel();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 37) { // Output slot
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 36 && index != 38) { // Not input or upgrade
                if (this.canSmelt(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39) {
                    if (!this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    private boolean canSmelt(ItemStack stack) {
        // Check if item can be smelted
        return level.getRecipeManager().getRecipeFor(
                net.minecraft.world.item.crafting.RecipeType.SMELTING,
                new net.minecraft.world.item.crafting.SingleRecipeInput(stack), level).isPresent();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

// Enhanced Growth Chamber Menu with Energy Support
/**
 * Enhanced Growth Chamber Menu with energy display
 * Extends your existing GrowthChamberMenu
 */
/*
// Add to your existing GrowthChamberMenu class:

private final EnhancedGrowthChamberBlockEntity enhancedBlockEntity;

// Add these methods:
public int getEnergyStored() {
    if (enhancedBlockEntity != null) {
        return enhancedBlockEntity.getEnergyStorageInternal().getEnergyStored();
    }
    return 0;
}

public int getMaxEnergyStored() {
    if (enhancedBlockEntity != null) {
        return enhancedBlockEntity.getEnergyStorageInternal().getMaxEnergyStored();
    }
    return 0;
}

public int getEnergyBarHeight() {
    int maxEnergy = getMaxEnergyStored();
    return maxEnergy != 0 ? (getEnergyStored() * 52) / maxEnergy : 0;
}

public boolean isUsingEnergy() {
    return enhancedBlockEntity != null && enhancedBlockEntity.isUsingEnergy();
}
*/

