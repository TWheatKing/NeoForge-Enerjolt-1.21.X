package com.crofty.enerjolt.block.entity.energy;

import com.crofty.enerjolt.block.custom.energy.ElectricSmelterBlock;
import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricSmelterBlockEntity extends BlockEntity implements MenuProvider,
        EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier tier;
    private final EnerjoltEnergyStorage energyStorage;
    private final ItemStackHandler itemHandler;

    // Processing variables
    private int processTime = 0;
    private int maxProcessTime = 200; // Base processing time (10 seconds)
    private int energyPerTick = 20;   // Energy consumed per tick while processing
    private int speedLevel = 1;

    // Recipe caching
    private RecipeHolder<SmeltingRecipe> cachedRecipe;
    private ItemStack lastInput = ItemStack.EMPTY;

    // Capability caches for auto I/O
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] energyCaches;
    private final BlockCapabilityCache<net.neoforged.neoforge.items.IItemHandler, Direction>[] itemCaches;

    // Slots
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_SLOT = 2; // Speed upgrade slot

    @SuppressWarnings("unchecked")
    public ElectricSmelterBlockEntity(BlockPos pos, BlockState state, EnergyTier tier) {
        super(null, pos, state); // Register block entity type
        this.tier = tier;
        this.energyPerTick = calculateEnergyPerTick(tier);

        // Create energy storage
        this.energyStorage = new EnerjoltEnergyStorage(
        ) {
            @Override
            public int receiveEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public int extractEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return 0;
            }

            @Override
            public int getMaxEnergyStored() {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public EnergyTier getTier() {
                return null;
            }

            @Override
            public float getEfficiency() {
                return 0;
            }

            @Override
            public void setEfficiency(float efficiency) {

            }

            @Override
            public float getStoredPercentage() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean isFull() {
                return false;
            }

            @Override
            public void setEnergyStored(int energy) {

            }

            @Override
            public CompoundTag serializeNBT(HolderLookup.Provider provider) {
                return null;
            }

            @Override
            public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {

            }
        };

        // Create item handler (input, output, upgrade)
        this.itemHandler = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();

                // Invalidate recipe cache if input changed
                if (slot == INPUT_SLOT) {
                    cachedRecipe = null;
                }

                // Update speed level if upgrade changed
                if (slot == UPGRADE_SLOT) {
                    updateSpeedLevel();
                }
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return switch (slot) {
                    case INPUT_SLOT -> true; // Any item can be input
                    case OUTPUT_SLOT -> false; // Output slot only
                    case UPGRADE_SLOT -> isSpeedUpgrade(stack); // Only speed upgrades
                    default -> false;
                };
            }
        };

        // Initialize capability caches
        energyCaches = new BlockCapabilityCache[6];
        itemCaches = new BlockCapabilityCache[6];
        if (level != null) {
            initializeCaches();
        }
    }

    private int calculateEnergyCapacity(EnergyTier tier) {
        return tier.getVoltage() * 10; // 10x voltage as capacity
    }

    private int calculateEnergyPerTick(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 20;       // 20 EU/t
            case MEDIUM -> 40;    // 40 EU/t
            case HIGH -> 80;      // 80 EU/t
            case EXTREME -> 160;  // 160 EU/t
            case INSANE -> 320;   // 320 EU/t
            case LUDICROUS -> 640; // 640 EU/t
            case ZENITH -> 1280;  // 1280 EU/t
            case ULTIMATE -> 2560; // 2560 EU/t
            case CREATIVE -> 0;   // Free
        };
    }

    private void initializeCaches() {
        if (level == null) return;

        for (Direction direction : Direction.values()) {
            int index = direction.get3DDataValue();
            BlockPos neighborPos = worldPosition.relative(direction);

            energyCaches[index] = BlockCapabilityCache.create(
                    Capabilities.EnergyStorage.BLOCK, (ServerLevel) level, neighborPos, direction.getOpposite());
            itemCaches[index] = BlockCapabilityCache.create(
                    Capabilities.ItemHandler.BLOCK, (ServerLevel) level, neighborPos, direction.getOpposite());
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean wasProcessing = isProcessing();

        // Auto-input items
        autoInputItems();

        // Process items
        processItems();

        // Auto-output items
        autoOutputItems();

        // Update block state if needed
        if (wasProcessing != isProcessing()) {
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(ElectricSmelterBlock.ACTIVE, isProcessing()));
        }

        setChanged();
    }

    private void autoInputItems() {
        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.getCount() >= inputStack.getMaxStackSize()) return;

        // Try to pull items from adjacent inventories
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue; // Don't pull from bottom (output)

            var itemCache = itemCaches[direction.get3DDataValue()];
            if (itemCache != null) {
                var neighborHandler = itemCache.getCapability();
                if (neighborHandler != null) {
                    // Try to extract items that can be smelted
                    for (int slot = 0; slot < neighborHandler.getSlots(); slot++) {
                        ItemStack extractTest = neighborHandler.extractItem(slot, 1, true);
                        if (!extractTest.isEmpty() && canSmelt(extractTest)) {
                            ItemStack extracted = neighborHandler.extractItem(slot,
                                    Math.min(extractTest.getCount(),
                                            inputStack.getMaxStackSize() - inputStack.getCount()), false);

                            if (!extracted.isEmpty()) {
                                if (inputStack.isEmpty()) {
                                    itemHandler.setStackInSlot(INPUT_SLOT, extracted);
                                } else if (ItemStack.isSameItemSameComponents(inputStack, extracted)) {
                                    inputStack.grow(extracted.getCount());
                                    itemHandler.setStackInSlot(INPUT_SLOT, inputStack);
                                }
                                return; // Only pull from one source per tick
                            }
                        }
                    }
                }
            }
        }
    }

    private void processItems() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack output = itemHandler.getStackInSlot(OUTPUT_SLOT);

        // Check if we can start/continue processing
        if (input.isEmpty() || !hasEnoughEnergy()) {
            if (processTime > 0) {
                processTime = 0; // Stop processing
            }
            return;
        }

        // Get or cache recipe
        Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = getRecipe(input);
        if (recipeOpt.isEmpty()) {
            processTime = 0;
            return;
        }

        RecipeHolder<SmeltingRecipe> recipe = recipeOpt.get();
        ItemStack result = recipe.value().getResultItem(level.registryAccess());

        // Check if output slot can accept result
        if (!output.isEmpty() &&
                (!ItemStack.isSameItemSameComponents(output, result) ||
                        output.getCount() + result.getCount() > output.getMaxStackSize())) {
            return; // Cannot output
        }

        // Process
        if (processTime == 0) {
            // Start processing
            maxProcessTime = calculateProcessTime(recipe.value().getCookingTime());
            processTime = 1;

            // Play start sound
            level.playSound(null, worldPosition, SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
        }

        // Continue processing
        if (consumeEnergy()) {
            processTime++;

            // Finish processing
            if (processTime >= maxProcessTime) {
                finishProcessing(input, result);
                processTime = 0;
            }
        }
    }

    private void finishProcessing(ItemStack input, ItemStack result) {
        // Consume input
        input.shrink(1);
        itemHandler.setStackInSlot(INPUT_SLOT, input);

        // Produce output
        ItemStack output = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }

        // Play completion sound
        level.playSound(null, worldPosition, SoundEvents.FURNACE_FIRE_CRACKLE,
                SoundSource.BLOCKS, 0.5f, 1.5f);
    }

    private void autoOutputItems() {
        ItemStack output = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) return;

        // Try to push to adjacent inventories (prioritize bottom)
        Direction[] priorities = {Direction.DOWN, Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST, Direction.UP};

        for (Direction direction : priorities) {
            var itemCache = itemCaches[direction.get3DDataValue()];
            if (itemCache != null) {
                var neighborHandler = itemCache.getCapability();
                if (neighborHandler != null) {
                    // Try to insert items
                    for (int slot = 0; slot < neighborHandler.getSlots(); slot++) {
                        ItemStack remaining = neighborHandler.insertItem(slot, output, false);
                        if (remaining.getCount() < output.getCount()) {
                            itemHandler.setStackInSlot(OUTPUT_SLOT, remaining);
                            return; // Successfully transferred some items
                        }
                    }
                }
            }
        }
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getRecipe(ItemStack input) {
        // Use cached recipe if input hasn't changed
        if (cachedRecipe != null && ItemStack.isSameItemSameComponents(lastInput, input)) {
            return Optional.of(cachedRecipe);
        }

        // Find new recipe
        if (level != null) {
            SingleRecipeInput recipeInput = new SingleRecipeInput(input);
            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING, recipeInput, level);

            if (recipe.isPresent()) {
                cachedRecipe = recipe.get();
                lastInput = input.copy();
                return recipe;
            }
        }

        cachedRecipe = null;
        lastInput = ItemStack.EMPTY;
        return Optional.empty();
    }

    private boolean canSmelt(ItemStack input) {
        return getRecipe(input).isPresent();
    }

    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored() >= energyPerTick || tier == EnergyTier.CREATIVE;
    }

    private boolean consumeEnergy() {
        if (tier == EnergyTier.CREATIVE) return true;
        return energyStorage.extractEnergy(energyPerTick, false) >= energyPerTick;
    }

    private int calculateProcessTime(int baseTime) {
        // Reduce process time based on speed level and tier
        float speedMultiplier = switch (speedLevel) {
            case 1 -> 1.0f;
            case 2 -> 0.8f;  // 20% faster
            case 3 -> 0.6f;  // 40% faster
            case 4 -> 0.4f;  // 60% faster
            case 5 -> 0.2f;  // 80% faster
            default -> 1.0f;
        };

        // Higher tiers are naturally faster
        float tierMultiplier = switch (tier) {
            case LOW -> 1.0f;
            case MEDIUM -> 0.9f;
            case HIGH -> 0.8f;
            case EXTREME -> 0.7f;
            case INSANE -> 0.6f;
            case LUDICROUS -> 0.5f;
            case ZENITH -> 0.4f;
            case ULTIMATE -> 0.3f;
            case CREATIVE -> 0.1f; // Very fast
        };

        return Math.max(1, (int) (baseTime * speedMultiplier * tierMultiplier));
    }

    private void updateSpeedLevel() {
        ItemStack upgrade = itemHandler.getStackInSlot(UPGRADE_SLOT);
        speedLevel = getSpeedLevel(upgrade);

        // Update block state
        if (level != null) {
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(ElectricSmelterBlock.SPEED_LEVEL, speedLevel));
        }
    }

    private boolean isSpeedUpgrade(ItemStack stack) {
        // Check if stack is a speed upgrade (implement based on your upgrade items)
        return false; // Placeholder
    }

    private int getSpeedLevel(ItemStack upgrade) {
        if (upgrade.isEmpty()) return 1;
        // Return speed level based on upgrade (1-5)
        return 1; // Placeholder
    }

    public boolean isProcessing() {
        return processTime > 0;
    }

    public int getProcessProgress() {
        return maxProcessTime > 0 ? (processTime * 100) / maxProcessTime : 0;
    }

    public int getEnergyProgress() {
        return (int) (energyStorage.getStoredPercentage() * 100);
    }

    public void drops() {
        if (level != null) {
            net.minecraft.world.Containers.dropContents(level, worldPosition,
                    new SimpleContainer(itemHandler.getSlots()) {{
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            setItem(i, itemHandler.getStackInSlot(i));
                        }
                    }});
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.putInt("ProcessTime", processTime);
        tag.putInt("MaxProcessTime", maxProcessTime);
        tag.putInt("SpeedLevel", speedLevel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        processTime = tag.getInt("ProcessTime");
        maxProcessTime = tag.getInt("MaxProcessTime");
        speedLevel = tag.getInt("SpeedLevel");
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        initializeCaches();
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.enerjolt.electric_smelter_" + tier.name().toLowerCase());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Return smelter menu
        return null; // Implementation depends on your GUI system
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return new EnergyCapabilityProvider.DirectionalEnergyStorage(
                energyStorage, direction, true, false);
    }

    @Override
    public EnergyTier getEnergyTier() {
        return tier;
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        return direction != Direction.DOWN; // Don't connect energy from bottom
    }

    // Public getters
    public EnerjoltEnergyStorage getEnergyStorageInternal() {
        return energyStorage;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public EnergyTier getTier() {
        return tier;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }
}
