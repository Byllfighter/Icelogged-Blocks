package com.nottouchedgrass.icelogged.client.mixin;

import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Inject(
            method = "shouldRenderFace",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void shouldRenderFaceMixin(
            BlockAndTintGetter level, BlockState state, Direction direction, BlockPos neighborPos, CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            if (level.getBlockState(neighborPos).is(IceloggedMod.ICELOGGED_BLOCK)) {
                BlockEntity blockEntity = level.getBlockEntity(neighborPos);
                if (blockEntity instanceof IceloggedBlockEntity iceloggedBlockEntity) {
                    iceloggedBlockEntity.innerState.ifPresent(innerState -> {
                        cir.setReturnValue(Block.shouldRenderFace(state, innerState, direction));
                    });
                }
            }
        }
    }
}
