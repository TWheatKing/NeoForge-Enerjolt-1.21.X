package com.crofty.enerjolt.block.custom.energy;

import com.crofty.enerjolt.block.entity.energy.EnergyCableBlockEntity;
import com.crofty.enerjolt.energy.EnergyCapabilityProvider;
import com.crofty.enerjolt.energy.EnergyTier;
import com.crofty.enerjolt.energy.EnerjoltEnergyStorage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Energy Cable Block - Transfers energy between machines
 */
public class EnergyCableBlock extends BaseEntityBlock {

    // Connection properties for each direction
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape CORE_SHAPE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape[] CONNECTION_SHAPES = {
            Block.box(6, 0, 6, 10, 6, 10),  // DOWN
            Block.box(6, 10, 6, 10, 16, 10), // UP
            Block.box(6, 6, 0, 10, 10, 6),   // NORTH
            Block.box(6, 6, 10, 10, 10, 16), // SOUTH
            Block.box(0, 6, 6, 6, 10, 10),   // WEST
            Block.box(10, 6, 6, 16, 10, 10)  // EAST
    };

    private final EnergyTier tier;
    private final int transferRate;

    public EnergyCableBlock(Properties properties, EnergyTier tier) {
        super(properties);
        this.tier = tier;
        this.transferRate = tier.getVoltage();

        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE_SHAPE;

        if (state.getValue(DOWN)) shape = Shapes.or(shape, CONNECTION_SHAPES[0]);
        if (state.getValue(UP)) shape = Shapes.or(shape, CONNECTION_SHAPES[1]);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, CONNECTION_SHAPES[2]);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, CONNECTION_SHAPES[3]);
        if (state.getValue(WEST)) shape = Shapes.or(shape, CONNECTION_SHAPES[4]);
        if (state.getValue(EAST)) shape = Shapes.or(shape, CONNECTION_SHAPES[5]);

        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyCableBlockEntity(pos, state, tier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;

        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof EnergyCableBlockEntity cable) {
                cable.tick();
            }
        };
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            updateConnections(level, pos);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide) {
            updateConnections(level, pos);
        }
    }

    private void updateConnections(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof EnergyCableBlockEntity cableEntity) {
            BlockState newState = getBlockState(level, pos);
            if (!newState.equals(level.getBlockState(pos))) {
                level.setBlockAndUpdate(pos, newState);
                cableEntity.updateConnections();
            }
        }
    }

    private BlockState getBlockState(Level level, BlockPos pos) {
        BlockState state = this.defaultBlockState();

        for (Direction direction : Direction.values()) {
            boolean canConnect = canConnectTo(level, pos, direction);
            state = state.setValue(getConnectionProperty(direction), canConnect);
        }

        return state;
    }

    private boolean canConnectTo(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

        // Check if neighbor is an energy handler
        if (neighborEntity instanceof EnergyCapabilityProvider.IEnergyHandler energyHandler) {
            return energyHandler.canConnectEnergy(direction.getOpposite()) &&
                    tier.isCompatibleWith(energyHandler.getEnergyTier());
        }

        // Check if neighbor has energy capability
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                neighborPos, direction.getOpposite());
        return energyStorage != null;
    }

    private BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public EnergyTier getTier() {
        return tier;
    }

    public int getTransferRate() {
        return transferRate;
    }
}

/**
 * Cable Network for optimized energy distribution
 */
class CableNetwork {
    private final Set<BlockPos> cablePositions = new HashSet<>();
    private final EnergyTier tier;
    private boolean dirty = false;

    private CableNetwork(EnergyTier tier) {
        this.tier = tier;
    }

    public static CableNetwork buildNetwork(Level level, BlockPos startPos, EnergyTier tier) {
        CableNetwork network = new CableNetwork(tier);
        network.buildFromPosition(level, startPos, new HashSet<>());
        return network;
    }

    private void buildFromPosition(Level level, BlockPos pos, Set<BlockPos> visited) {
        if (visited.contains(pos)) return;
        visited.add(pos);

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof EnergyCableBlockEntity cable)) return;
        if (!cable.getEnergyTier().equals(tier)) return;

        cablePositions.add(pos);

        // Recursively find connected cables
        for (Direction direction : Direction.values()) {
            if (cable.isConnected(direction)) {
                BlockPos neighborPos = pos.relative(direction);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (neighbor instanceof EnergyCableBlockEntity) {
                    buildFromPosition(level, neighborPos, visited);
                }
            }
        }
    }

    public Set<BlockPos> getCablePositions() {
        return cablePositions;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }
}