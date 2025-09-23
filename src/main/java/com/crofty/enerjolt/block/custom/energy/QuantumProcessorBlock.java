package com.crofty.enerjolt.block.custom.energy;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable; /**
 * Quantum Processor - Ultra-advanced energy machine for complex recipes
 */
public class QuantumProcessorBlock extends BaseEntityBlock {
    public static final MapCodec<QuantumProcessorBlock> CODEC = simpleCodec(QuantumProcessorBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty QUANTUM_STATE = BooleanProperty.create("quantum_state");

    public QuantumProcessorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(ACTIVE, false)
                .setValue(QUANTUM_STATE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, QUANTUM_STATE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuantumProcessorBlockEntity(pos, state);
    }

    // Quantum processor only works with ULTIMATE or CREATIVE tier energy
    // Processes unique recipes at incredible speeds
    // Can transmute materials and create exotic matter
    // Requires quantum stabilization to prevent reality tears
}
