package com.nottouchedgrass.icelogged.mixin.client;

import com.nottouchedgrass.icelogged.Constants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HalfTransparentBlock.class)
public abstract class HalfTransparentBlockMixin {

    @Inject(
            method = "skipRendering",
            at = @At("HEAD"),
            cancellable = true
    )
    private void connectCustomIce(
            BlockState state,
            BlockState neighbor,
            Direction dir,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (state.is(Blocks.ICE)) {
            if (neighbor.is(BuiltInRegistries.BLOCK.getValue(Constants.ICELOGGED_BLOCK_ID))) {
                cir.setReturnValue(true);
            }
        }
    }
}
