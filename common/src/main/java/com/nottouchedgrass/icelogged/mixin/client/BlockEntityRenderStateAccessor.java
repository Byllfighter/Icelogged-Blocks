package com.nottouchedgrass.icelogged.mixin.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityRenderState.class)
public interface BlockEntityRenderStateAccessor {
    //@Accessor("blockState")
    //BlockState _MoreBlockItems$getBlockState();
    @Accessor("blockState")
    void _IceloggedBlocks$setBlockState(BlockState state);
}