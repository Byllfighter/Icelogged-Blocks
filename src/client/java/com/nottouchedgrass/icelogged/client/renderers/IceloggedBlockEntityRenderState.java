package com.nottouchedgrass.icelogged.client.renderers;

import com.nottouchedgrass.icelogged.client.IceloggedBlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class IceloggedBlockEntityRenderState extends BlockEntityRenderState {
    public Level level;
    public IceloggedBlockAndTintGetter blockAndTintGetter;
    public Optional<BlockState> innerState;
    public Optional<BlockEntity> innerBlockEntity;
    public Optional<BlockEntityRenderState> innerBlockEntityRenderState;
    public Optional<BlockEntityRenderer> innerBlockEntityRenderer;

    public IceloggedBlockEntityRenderState() {
    }
}