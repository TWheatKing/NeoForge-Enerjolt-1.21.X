package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import com.crofty.enerjolt.energy.ModEnergyComponents;
import com.crofty.enerjolt.item.custom.energy.EnergyToolItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Energy Storage Block - Stores energy for later use
 */
public class EnergyStorageBlock extends BaseEntityBlock {
    public static final MapCodec<EnergyStorageBlock> CODEC = simpleCodec(EnergyStorageBlock::new);
    public static final IntegerProperty CHARGE_LEVEL = IntegerProperty.create("charge_level", 0, 8);

    private final EnergyTier tier;

    public EnergyStorageBlock(Properties properties, EnergyTier tier) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.defaultBlockState().setValue(CHARGE_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE_LEVEL);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(pos, state, tier);
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
            if (blockEntity instanceof EnergyStorageBlockEntity storage) {
                storage.tick();
            }
        };
    }

    public EnergyTier getTier() {
        return tier;
    }
}

/**
 * Block Entity for Energy Storage
 */
class EnergyStorageBlockEntity extends BlockEntity implements MenuProvider,
        EnergyCapabilityProvider.IEnergyHandler {

    private final EnergyTier tier;
    private final EnerjoltEnergyStorage energyStorage;
    private int lastChargeLevel = -1;

    // Capability caches for adjacent blocks
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] energyCaches;

    @SuppressWarnings("unchecked")
    public EnergyStorageBlockEntity(BlockPos pos, BlockState state, EnergyTier tier) {
        super(null, pos, state); // Register block entity type
        this.tier = tier;

        // Create large energy storage
        int capacity = calculateCapacity(tier);
        int transferRate = tier.getVoltage() * 2; // Can charge/discharge at 2x voltage

        this.energyStorage = new EnerjoltEnergyStorage(
                capacity, transferRate, transferRate, tier, true, true, tier != EnergyTier.CREATIVE);

        // Initialize capability caches
        energyCaches = new BlockCapabilityCache[6];
        if (level != null) {
            for (Direction direction : Direction.values()) {
                energyCaches[direction.get3DDataValue()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, level, pos.relative(direction), direction.getOpposite());
            }
        }
    }

    private int calculateCapacity(EnergyTier tier) {
        return switch (tier) {
            case LOW -> 32000;      // 32k EU
            case MEDIUM -> 128000;  // 128k EU
            case HIGH -> 512000;    // 512k EU
            case EXTREME -> 2048000; // 2M EU
            case INSANE -> 8192000; // 8M EU
            case LUDICROUS -> 32768000; // 32M EU
            case ZENITH -> 131072000; // 131M EU
            case ULTIMATE -> 524288000; // 524M EU
            case CREATIVE -> Integer.MAX_VALUE; // Infinite
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Auto-output energy to adjacent consumers if configured
        distributeEnergyToAdjacent();

        // Update visual charge level
        updateChargeLevel();

        setChanged();
    }

    private void distributeEnergyToAdjacent() {
        if (energyStorage.getEnergyStored() == 0) return;

        int maxTransfer = tier.getVoltage();

        for (Direction direction : Direction.values()) {
            if (energyCaches[direction.get3DDataValue()] != null) {
                IEnergyStorage consumer = energyCaches[direction.get3DDataValue()].getCapability();
                if (consumer != null && consumer.canReceive()) {
                    EnergyCapabilityProvider.EnergyUtils.transferEnergy(
                            energyStorage, consumer, maxTransfer);
                }
            }
        }
    }

    private void updateChargeLevel() {
        int newChargeLevel = (int) (energyStorage.getStoredPercentage() * 8);
        if (newChargeLevel != lastChargeLevel) {
            lastChargeLevel = newChargeLevel;
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(EnergyStorageBlock.CHARGE_LEVEL, newChargeLevel));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStorage.deserializeNBT(registries, tag.getCompound("Energy"));
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.enerjolt.energy_storage_" + tier.name().toLowerCase());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Return your storage menu here
        return null; // Implementation depends on your GUI system
    }

    // IEnergyHandler implementation
    @Override
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }

    @Override
    public EnergyTier getEnergyTier() {
        return tier;
    }

    @Override
    public boolean canConnectEnergy(@Nullable Direction direction) {
        return true;
    }

    // Public getters
    public EnerjoltEnergyStorage getEnergyStorageInternal() {
        return energyStorage;
    }

    public EnergyTier getTier() {
        return tier;
    }
}