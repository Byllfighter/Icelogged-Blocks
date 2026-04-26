package com.nottouchedgrass.icelogged.blocks;

import com.mojang.serialization.MapCodec;
import com.nottouchedgrass.icelogged.blockentities.FrostedIceloggedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class FrostedIceloggedBlock extends IceloggedBlock implements EntityBlock {
    public static final MapCodec<FrostedIceloggedBlock> CODEC = simpleCodec(FrostedIceloggedBlock::new);

    public MapCodec<? extends FrostedIceloggedBlock> codec() {
        return CODEC;
    }

    public FrostedIceloggedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FrostedIceBlock.AGE, 0));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FrostedIceloggedBlockEntity(blockPos, blockState);
    }

    @Override
    protected boolean skipRendering(
            BlockState state,
            BlockState neighborState,
            Direction direction
    ) {
        if (neighborState.is(Blocks.FROSTED_ICE) || neighborState.is(this)) {
            return true;
        }

        return super.skipRendering(state, neighborState, direction);
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 60, 120));
    }

    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0 || this.fewerNeigboursThan(level, pos, 4)) {
            int brightness = level.dimension() == Level.END ? level.getBrightness(LightLayer.BLOCK, pos) : level.getMaxLocalRawBrightness(pos);
            if (brightness > 11 - (Integer)state.getValue(FrostedIceBlock.AGE) - state.getLightDampening() && this.slightlyMelt(state, level, pos)) {
                BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

                for(Direction direction : Direction.values()) {
                    neighborPos.setWithOffset(pos, direction);
                    BlockState neighbour = level.getBlockState(neighborPos);
                    if (neighbour.is(this) && !this.slightlyMelt(neighbour, level, neighborPos)) {
                        level.scheduleTick(neighborPos, this, Mth.nextInt(random, 20, 40));
                    }
                }

                return;
            }
        }

        level.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));
    }

    private boolean slightlyMelt(BlockState state, Level level, BlockPos pos) {
        int age = (Integer)state.getValue(FrostedIceBlock.AGE);
        if (age < 3) {
            level.setBlock(pos, (BlockState)state.setValue(FrostedIceBlock.AGE, age + 1), 2);
            return false;
        } else {
            this.melt(state, level, pos, level.getBlockEntity(pos));
            return true;
        }
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        if ((block.defaultBlockState().is(this) || block.defaultBlockState().is(Blocks.FROSTED_ICE)) && this.fewerNeigboursThan(level, pos, 2)) {
            this.melt(state, level, pos, level.getBlockEntity(pos));
        }

        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    private boolean fewerNeigboursThan(BlockGetter level, BlockPos pos, int limit) {
        int result = 0;
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

        for(Direction direction : Direction.values()) {
            neighborPos.setWithOffset(pos, direction);
            if (level.getBlockState(neighborPos).is(this) || level.getBlockState(neighborPos).is(Blocks.FROSTED_ICE)) {
                ++result;
                if (result >= limit) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FrostedIceBlock.AGE);
    }

    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }
}