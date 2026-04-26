package com.nottouchedgrass.icelogged.mixin;

import com.nottouchedgrass.icelogged.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FrostedIceBlock.class)
public class FrostedIceBlockMixin {
    @Redirect(
            method = "neighborChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
            )
    )
    private boolean neighborChanged(BlockState instance, Object o) {
        if (instance.is((Block) o)) return true;
        if (o == this) {
            instance.is(BuiltInRegistries.BLOCK.getValue(Constants.FROSTED_ICELOGGED_BLOCK_ID));
        }
        return false;
    }

    @Redirect(
            method = "fewerNeigboursThan",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
            )
    )
    private boolean fewerNeigboursThan(BlockState instance, Object o) {
        if (instance.is((Block) o)) return true;
        if (o == this) {
            instance.is(BuiltInRegistries.BLOCK.getValue(Constants.FROSTED_ICELOGGED_BLOCK_ID));
        }
        return false;
    }
}
