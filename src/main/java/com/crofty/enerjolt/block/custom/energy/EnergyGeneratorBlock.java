package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Basic Energy Generator Block - Converts fuel to energy
 */
public class EnergyGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<EnergyGeneratorBlock> CODEC = simpleCodec(EnergyGeneratorBlock::new);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private final EnergyTier tier;

    public EnergyGeneratorBlock(Properties properties, EnergyTier tier) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    public EnergyGeneratorBlock(Properties properties) {
        this(properties, EnergyTier.LOW);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyGeneratorBlockEntity(pos, state, tier);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof MenuProvider menuProvider) {
                serverPlayer.openMenu(menuProvider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EnergyGeneratorBlockEntity generator) {
                generator.tick();
            }
        };
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof EnergyGeneratorBlockEntity generator) {
                generator.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public EnergyTier getTier() {
        return tier;
    }
}

/**
 * Block Entity for Energy Generator
 */
class EnergyGeneratorBlockEntity extends BlockEntity implements MenuProvider,
        EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier tier;
    private final EnerjoltEnergyStorage energyStorage;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int energyGenerationRate;

    // Fuel slot simulation (you'd integrate with actual item handler)
    private int fuelValue = 0;

    // Cache for nearby energy consumers
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] energyCaches;

    @SuppressWarnings("unchecked")
    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state, EnergyTier tier) {
        super(null, pos, state); // You'll need to register the block entity type
        this.tier = tier;
        this.energyGenerationRate = calculateGenerationRate(tier);

        // Create energy storage
        this.energyStorage = new EnerjoltEnergyStorage(
                tier.getMaxCapacity() / 4, // Buffer size
                0, // Can't receive energy
                tier.getVoltage(), // Can output energy
                tier,
                false, // Can't receive
                true,  // Can extract
                false  // No energy loss for generators
        );

        // Initialize capability caches for all directions
        energyCaches = new BlockCapabilityCache[6];
        if (level != null) {
            for (Direction direction : Direction.values()) {
                energyCaches[direction.get3DDataValue()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, level, pos.relative(direction), direction.getOpposite());
            }
        }
    }

    private int calculateGenerationRate(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 20;      // 20 EU/t
            case MEDIUM -> 80;   // 80 EU/t
            case HIGH -> 320;    // 320 EU/t
            case EXTREME -> 1280; // 1280 EU/t
            case INSANE -> 5120; // 5120 EU/t
            case LUDICROUS -> 20480; // 20480 EU/t
            case ZENITH -> 81920; // 81920 EU/t
            case ULTIMATE -> 327680; // 327680 EU/t
            case CREATIVE -> Integer.MAX_VALUE; // Infinite
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean wasGenerating = isGenerating();

        // Handle fuel burning
        if (burnTime > 0) {
            burnTime--;
            generateEnergy();
        } else {
            // Try to start burning fuel
            startBurning();
        }

        // Distribute energy to adjacent blocks
        distributeEnergy();

        // Update block state if generation status changed
        if (wasGenerating != isGenerating()) {
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(EnergyGeneratorBlock.POWERED, isGenerating()));
        }

        setChanged();
    }

    private void generateEnergy() {
        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
            energyStorage.receiveEnergy(energyGenerationRate, false);
        }
    }

    private void startBurning() {
        // Simplified fuel system - in real implementation, you'd check fuel items
        if (fuelValue > 0 && !energyStorage.isFull()) {
            maxBurnTime = burnTime = 200; // 10 seconds at 20 TPS
            fuelValue--; // Consume fuel
        }
    }

    private void distributeEnergy() {
        if (energyStorage.getEnergyStored() == 0) return;

        int energyPerDirection = energyStorage.getEnergyStored() / 6;
        if (energyPerDirection == 0) return;

        for (Direction direction : Direction.values()) {
            if (energyCaches[direction.get3DDataValue()] != null) {
                IEnergyStorage consumer = energyCaches[direction.get3DDataValue()].getCapability();
                if (consumer != null && consumer.canReceive()) {
                    EnergyCapabilityProvider.EnergyUtils.transferEnergy(
                            energyStorage, consumer, Math.min(energyPerDirection, tier.getVoltage()));
                }
            }
        }
    }

    public boolean isGenerating() {
        return burnTime > 0;
    }

    public int getBurnProgress() {
        return maxBurnTime > 0 ? (burnTime * 100) / maxBurnTime : 0;
    }

    public int getEnergyProgress() {
        return (int) (energyStorage.getStoredPercentage() * 100);
    }

    public void drops() {
        // Drop items when block is broken
        // Implementation depends on your item system
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
        tag.putInt("FuelValue", fuelValue);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
        fuelValue = tag.getInt("FuelValue");
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.enerjolt.energy_generator_" + tier.name().toLowerCase());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Return your generator menu here
        return null; // Implementation depends on your GUI system
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        // Only allow energy extraction, no input
        return new EnergyCapabilityProvider.DirectionalEnergyStorage(
                energyStorage, direction, false, true);
    }

    @Override
    public EnergyTier getEnergyTier() {
        return tier;
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        return true; // Can connect energy cables from any side
    }

    // Public getters for GUI
    public EnerjoltEnergyStorage getEnergyStorageInternal() {
        return energyStorage;
    }

    public EnergyTier getTier() {
        return tier;
    }

    public void addFuel(int amount) {
        this.fuelValue += amount;
        setChanged();
    }

    public int getFuelRemaining() {
        return fuelValue;
    }
}