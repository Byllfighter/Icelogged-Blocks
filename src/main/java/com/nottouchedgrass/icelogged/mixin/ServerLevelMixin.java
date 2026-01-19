package com.nottouchedgrass.icelogged.mixin;

import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Redirect(
            method = "tickPrecipitation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean allowLiquidContainers(ServerLevel instance, BlockPos blockPos, BlockState blockState) {
        if (blockState.is(Blocks.ICE)) {
            BlockState waterState = instance.getBlockState(blockPos);
            if (waterState.getBlock() instanceof LiquidBlockContainer && !(waterState.getBlock() instanceof LiquidBlock)) {
                BlockState state = IceloggedMod.ICELOGGED_BLOCK.defaultBlockState();
                IceloggedBlockEntity blockEntity = new IceloggedBlockEntity(blockPos, state);
                blockEntity.innerState = Optional.of(waterState.trySetValue(BlockStateProperties.WATERLOGGED, false));
                BlockEntity waterBlockEntity = instance.getBlockEntity(blockPos);
                if (waterBlockEntity != null) {
                    blockEntity.innerBlockEntity = Optional.of(waterBlockEntity.saveWithFullMetadata(instance.registryAccess()));
                }

                instance.removeBlockEntity(blockPos);
                boolean b = instance.setBlockAndUpdate(blockPos, state);
                if (b) {
                    instance.setBlockEntity(blockEntity);
                    //IceloggedMod.LOGGER.info("Frozen with "+ BuiltInRegistries.BLOCK.getKey(waterState.getBlock()).toString());
                    return b;
                }
            }
        }
        return instance.setBlockAndUpdate(blockPos, blockState);
    }
}
