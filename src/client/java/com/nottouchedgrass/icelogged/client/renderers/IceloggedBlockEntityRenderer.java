package com.nottouchedgrass.icelogged.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.client.IceloggedBlockAndTintGetter;
import com.nottouchedgrass.icelogged.client.mixin.BlockEntityRenderStateAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class IceloggedBlockEntityRenderer implements BlockEntityRenderer<IceloggedBlockEntity, IceloggedBlockEntityRenderState> {

    //BlockEntityRendererProvider.Context context;

    public IceloggedBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        // this.context = ctx;
    }

    @Override
    public IceloggedBlockEntityRenderState createRenderState() {
        return new IceloggedBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(IceloggedBlockEntity blockEntity, IceloggedBlockEntityRenderState state, float f, Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, f, vec3, crumblingOverlay);
        state.level = blockEntity.getLevel();
        state.blockAndTintGetter = new IceloggedBlockAndTintGetter((ClientLevel)blockEntity.getLevel());
        state.innerState = blockEntity.innerState;
        boolean hasBlockEntity = false;
        if (blockEntity.innerState.isPresent() && blockEntity.innerBlockEntity.isPresent()) {
            Minecraft mc = Minecraft.getInstance();
            BlockEntity innerBlockEntity = BlockEntity.loadStatic(state.blockPos, blockEntity.innerState.get(), blockEntity.innerBlockEntity.get(), blockEntity.getLevel().registryAccess());
            //innerBlockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, blockEntity.getLevel().registryAccess(), blockEntity.innerBlockEntity.get()));
            //innerBlockEntity.setBlockState(blockEntity.innerState.get());
            //((BlockEntityAccessor)innerBlockEntity).iceloggedblocks$setWorldPosition(blockEntity.getBlockPos());
            if (innerBlockEntity != null) {
                BlockEntityRenderer<BlockEntity, BlockEntityRenderState> blockEntityRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(innerBlockEntity);
                if (blockEntityRenderer != null) {
                    innerBlockEntity.setLevel(blockEntity.getLevel());
                    BlockEntityRenderState blockEntityState = blockEntityRenderer.createRenderState();
                    blockEntityRenderer.extractRenderState(innerBlockEntity, blockEntityState, mc.level.getGameTime(), mc.getCameraEntity().getLookAngle(), null);
                    ((BlockEntityRenderStateAccessor)blockEntityState)._IceloggedBlocks$setBlockState(blockEntity.innerState.get());

                    state.innerBlockEntity = Optional.of(innerBlockEntity);
                    state.innerBlockEntityRenderer = Optional.of(blockEntityRenderer);
                    state.innerBlockEntityRenderState = Optional.of(blockEntityState);
                    hasBlockEntity = true;
                }
            }
        }
        if (!hasBlockEntity) {
            state.innerBlockEntity = Optional.empty();
            state.innerBlockEntityRenderer = Optional.empty();
            state.innerBlockEntityRenderState = Optional.empty();
        }

    }

    @Override
    public void submit(IceloggedBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        state.innerState.ifPresent(blockState -> {
            Minecraft mc =  Minecraft.getInstance();
            poseStack.pushPose();
            poseStack.scale(0.9998F,0.9998F,0.9998F);
            poseStack.translate(0.0001F, 0.0001F, 0.0001F);

            ModelBlockRenderer modelBlockRenderer = new ModelBlockRenderer(mc.options.ambientOcclusion().get(), mc.options.cutoutLeaves().get(), mc.getBlockColors());
            modelBlockRenderer.tesselateBlock((x, y, z, quad, instance) -> {
                instance.setLightCoords(state.lightCoords);
                //instance.setOverlayCoords(overlayCoords);
                mc.renderBuffers().bufferSource().getBuffer(quad.materialInfo().itemRenderType()).putBakedQuad(poseStack.last(), quad, instance);
            }, 0f, 0f, 0f, state.blockAndTintGetter, state.blockPos, blockState, mc.getModelManager().getBlockStateModelSet().get(blockState), blockState.getSeed(state.blockPos));
            //mc.getBlockRenderer().renderBatched(blockState, state.blockPos, state.blockAndTintGetter, poseStack, mc.renderBuffers().bufferSource().getBuffer(RenderTypes.translucentMovingBlock()), true, mc.getBlockRenderer().getBlockModel(blockState).collectParts(state.level.random));
            //Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(state.level, mc.getBlockRenderer().getBlockModel(blockState).collectParts(state.level.random), blockState, state.blockPos, poseStack, mc.renderBuffers().bufferSource().getBuffer(RenderTypes.translucentMovingBlock()), true, state.lightCoords);

            state.innerBlockEntity.ifPresent(blockEntity -> {
                state.innerBlockEntityRenderer.ifPresent(blockEntityRenderer -> {
                    state.innerBlockEntityRenderState.ifPresent(blockEntityRenderState -> {
                        blockEntityRenderer.submit(blockEntityRenderState, poseStack, submitNodeCollector, cameraRenderState);
                    });
                });
            });
            poseStack.popPose();
        });
    }
}