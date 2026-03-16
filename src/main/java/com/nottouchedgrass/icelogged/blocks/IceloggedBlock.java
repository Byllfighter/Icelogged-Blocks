package com.nottouchedgrass.icelogged.blocks;

import com.mojang.serialization.MapCodec;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.Nullable;

public class IceloggedBlock extends HalfTransparentBlock implements EntityBlock {
    public static final MapCodec<IceloggedBlock> CODEC = simpleCodec(IceloggedBlock::new);

    public MapCodec<? extends IceloggedBlock> codec() {
        return CODEC;
    }

    public IceloggedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IceloggedBlockEntity(blockPos, blockState);
    }

    @Override
    protected boolean skipRendering(
            BlockState state,
            BlockState neighborState,
            Direction direction
    ) {
        if (neighborState.is(Blocks.ICE) || neighborState.is(this)) {
            return true;
        }

        return super.skipRendering(state, neighborState, direction);
    }

    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @org.jspecify.annotations.Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
        if (!EnchantmentHelper.hasTag(itemStack, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            this.melt(blockState, level, blockPos, blockEntity);
        }
    }


    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.getBrightness(LightLayer.BLOCK, blockPos) > 11 - blockState.getLightBlock()) {
            this.melt(blockState, serverLevel, blockPos, serverLevel.getBlockEntity(blockPos));
        }
    }

    protected void melt(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity) {
        if (blockEntity instanceof IceloggedBlockEntity iceloggedBlockEntity) {
            BlockState below = level.getBlockState(blockPos.below());
            boolean hasWater = (!level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, blockPos)) && (below.blocksMotion() || below.liquid());
            if (iceloggedBlockEntity.innerState.isPresent()) {
                BlockState innerState = iceloggedBlockEntity.innerState.get();
                if (hasWater) {
                    innerState = innerState.trySetValue(BlockStateProperties.WATERLOGGED, true);
                }
                level.removeBlockEntity(blockPos);
                level.setBlockAndUpdate(blockPos, innerState);
                if (!innerState.getFluidState().isEmpty()) {
                    level.scheduleTick(
                            blockPos,
                            innerState.getFluidState().getType(),
                            innerState.getFluidState().getType().getTickDelay(level)
                    );
                }
                BlockEntity newBlockEntity = level.getBlockEntity(blockPos);
                if (newBlockEntity != null && iceloggedBlockEntity.innerBlockEntity.isPresent()) {;
                    newBlockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), iceloggedBlockEntity.innerBlockEntity.get()));
                    newBlockEntity.setChanged();
                    level.blockEntityChanged(blockPos);
                    level.players().forEach(entityPlayer -> {
                        if (Mth.sqrt((float) entityPlayer.distanceToSqr(blockPos.getX(),blockPos.getY(),blockPos.getZ())) < 1024) {
                            //((ServerPlayer) entityPlayer).connection.send(newBlockEntity.getUpdatePacket());
                            ServerPlayNetworking.send((ServerPlayer) entityPlayer, new S2CUpdateBlockEntity(blockPos, newBlockEntity.saveCustomOnly(level.registryAccess())));
                        }
                    });
                }
                level.sendBlockUpdated(
                        blockPos,
                        innerState,
                        innerState,
                        3
                );
                /*
                iceloggedBlockEntity.innerBlockEntity.ifPresent(compoundTag -> {
                    BlockEntity innerBlockEntity = BlockEntity.loadStatic(blockPos, iceloggedBlockEntity.innerState.get(), compoundTag, level.registryAccess());
                    if (innerBlockEntity != null) {
                        level.setBlockEntity(innerBlockEntity);
                    }
                });
                 */
            } else {
                if (hasWater) {
                    level.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
                }
            }
        }
    }
}