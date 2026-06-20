package com.nottouchedgrass.icelogged.client.renderers.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;

import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntityRenderState;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class IceloggedInnerBlockFeatureRenderer extends RenderTypeFeatureRenderer<IceloggedInnerBlockFeatureRenderer.Submit> {
    public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("Icelogged Block");
    private final PoseStack poseStack = new PoseStack();

    protected void buildGroup(FeatureFrameContext context, List<Submit> submits) {
        boolean ambientOcclusion = context.options().ambientOcclusion;
        boolean cutoutLeaves = context.options().cutoutLeaves;
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, true, context.blockColors());

        for(Submit submit : submits) {
            IceloggedBlockEntityRenderState state = submit.iceloggedBlockEntityRenderState();
            BlockState blockState = state.innerState.orElse(null);
            if (blockState != null) {
                BlockStateModel model = context.blockStateModelSet().get(blockState);
                this.poseStack.setIdentity();
                this.poseStack.mulPose(submit.pose());
                BlockQuadOutput quadOutput = (x, y, z, quad, instance) -> {
                    instance.setLightCoords(state.lightCoords);
                    this.putBakedQuad(this.poseStack, x, y, z, quad, instance, quad.materialInfo().layer(), submit.outlineColor());
                };
                BlockQuadOutput solidQuadOutput = (x, y, z, quad, instance) -> {
                    instance.setLightCoords(state.lightCoords);
                    this.putBakedQuad(this.poseStack, x, y, z, quad, instance, ChunkSectionLayer.SOLID, submit.outlineColor());
                };
                BlockQuadOutput blockOutput = ModelBlockRenderer.forceOpaque(cutoutLeaves, blockState) ? solidQuadOutput : quadOutput;
                long blockSeed = blockState.getSeed(state.blockPos);
                blockRenderer.tesselateBlock(blockOutput, 0.0F, 0.0F, 0.0F, state.blockAndTintGetter, state.blockPos, blockState, model, blockSeed);
            }
        }
    }

    private void putBakedQuad(PoseStack poseStack, float x, float y, float z, BakedQuad quad, QuadInstance instance, ChunkSectionLayer layer, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        RenderType var10000;
        switch (layer) {
            case SOLID -> var10000 = RenderTypes.solidMovingBlock();
            case CUTOUT -> var10000 = RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> var10000 = RenderTypes.translucentMovingBlock();
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        RenderType renderType = var10000;
        VertexConsumer buffer;
        if (outlineColor != 0 && renderType.outline().isPresent()) {
            instance.setColor(outlineColor);
            buffer = this.getVertexBuilder((RenderType)renderType.outline().get());
        } else {
            buffer = this.getVertexBuilder(renderType);
        }

        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }

    public record Submit(Matrix4fc pose, IceloggedBlockEntityRenderState iceloggedBlockEntityRenderState, int outlineColor) implements TranslucentSubmit {
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose, 0.5F, 0.5F, 0.5F);
        }

        public FeatureRendererType<Submit> featureType() {
            return IceloggedInnerBlockFeatureRenderer.TYPE;
        }
    }
}

