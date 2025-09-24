package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.block.entity.energy.EnergyTransformerBlockEntity;
import com.crofty.enerjolt.energy.EnergyTier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Energy Transformer Block - Converts between voltage tiers
 * Can step up (low to high voltage) or step down (high to low voltage)
 */
public class EnergyTransformerBlock extends DirectionalBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty OVERLOAD = BooleanProperty.create("overload");

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    private final EnergyTier inputTier;
    private final EnergyTier outputTier;
    private final boolean isStepUp;

    public EnergyTransformerBlock(Properties properties, EnergyTier inputTier, EnergyTier outputTier) {
        super(properties);
        this.inputTier = inputTier;
        this.outputTier = outputTier;
        this.isStepUp = outputTier.ordinal() > inputTier.ordinal();

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false)
                .setValue(OVERLOAD, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE, OVERLOAD);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyTransformerBlockEntity(pos, state, inputTier, outputTier);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EnergyTransformerBlockEntity transformer) {
                transformer.tick();
            }
        };
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock() && state.getValue(OVERLOAD)) {
            // Create explosion if removed while overloaded
            level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    2.0f, Level.ExplosionInteraction.BLOCK);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public EnergyTier getInputTier() {
        return inputTier;
    }

    public EnergyTier getOutputTier() {
        return outputTier;
    }

    public boolean isStepUp() {
        return isStepUp;
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return null;
    }
}

