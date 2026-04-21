package com.nottouchedgrass.icelogged.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.nottouchedgrass.icelogged.Constants;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public record IceloggedBlockEntitySpecialRenderer(IceloggedBlockEntityRenderer renderer) implements SpecialModelRenderer<ItemStack> {

    public static class CachedIceloggedBlockEntity {
        public int time;
        public IceloggedBlockEntity iceloggedBlockEntity;
        CachedIceloggedBlockEntity(int time, IceloggedBlockEntity iceloggedBlockEntity) {
            this.time = time;
            this.iceloggedBlockEntity = iceloggedBlockEntity;
        }
    }
    public static final Map<IceloggedComponent, CachedIceloggedBlockEntity> CACHED_ICELOGGED_BLOCKENTITIES = new ConcurrentHashMap<>();

    @Override
    public void submit(@org.jspecify.annotations.Nullable ItemStack stack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        IceloggedComponent iceloggedComponent = stack.get((DataComponentType<IceloggedComponent>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Constants.ICELOGGED_COMPONENT_ID));
        if (iceloggedComponent != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                poseStack.pushPose();
                poseStack.scale(0.998f, 0.998f, 0.998f);
                poseStack.translate(0.001f, 0.001f, 0.001f);

                IceloggedBlockEntity blockEntity;
                if (CACHED_ICELOGGED_BLOCKENTITIES.containsKey(iceloggedComponent)) {
                    blockEntity = CACHED_ICELOGGED_BLOCKENTITIES.get(iceloggedComponent).iceloggedBlockEntity;
                } else {
                    blockEntity = iceloggedComponent.createBlockEntity(mc.level, new BlockPos(0, mc.level.getMaxY()+5, 0));
                    CACHED_ICELOGGED_BLOCKENTITIES.put(iceloggedComponent, new CachedIceloggedBlockEntity(5, blockEntity));
                }
                IceloggedBlockEntityRenderState renderState = renderer.createRenderState();
                renderer.extractRenderState(blockEntity, renderState, mc.level.getGameTime(), mc.getCameraEntity().getLookAngle(), null);
                renderState.lightCoords = lightCoords;
                renderer.submit(renderState, poseStack, submitNodeCollector, mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState);

                poseStack.popPose();
            }
        }




            /*
            if (blockEntityData.type() == IceloggedMod.ICELOGGED_BLOCK_ENTITY) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null) {
                    poseStack.pushPose();
                    poseStack.scale(0.998f, 0.998f, 0.998f);
                    poseStack.translate(0.001f, 0.001f, 0.001f);

                    IceloggedBlockEntity blockEntity;
                    if (CACHED_ICELOGGED_BLOCKENTITIES.containsKey(blockEntityData)) {
                        blockEntity = CACHED_ICELOGGED_BLOCKENTITIES.get(blockEntityData).iceloggedBlockEntity;
                    } else {
                        blockEntity = new IceloggedBlockEntity(new BlockPos(0, mc.level.getMaxY() + 5, 0), IceloggedMod.ICELOGGED_BLOCK.defaultBlockState());
                        blockEntity.setLevel(mc.level);
                        blockEntityData.loadInto(blockEntity, mc.level.registryAccess());
                        CACHED_ICELOGGED_BLOCKENTITIES.put(blockEntityData, new CachedIceloggedBlockEntity(5, blockEntity));
                    }
                    IceloggedBlockEntityRenderState renderState = renderer.createRenderState();
                    renderer.extractRenderState(blockEntity, renderState, mc.level.getGameTime(), mc.getCameraEntity().getLookAngle(), null);
                    renderState.lightCoords = i;
                    renderer.submit(renderState, poseStack, submitNodeCollector, Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState);

                    poseStack.popPose();
                }
            }
        }

             */
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0f, 0.0f, 0.0f));
        consumer.accept(new Vector3f(1.0f, 1.0f, 1.0f));
    }


    public @NonNull ItemStack extractArgument(@NonNull ItemStack stack) {
        return stack;
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        public @NonNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public SpecialModelRenderer bake(SpecialModelRenderer.@NonNull BakingContext bakingContext) {
            Minecraft mc = Minecraft.getInstance();
            return new IceloggedBlockEntitySpecialRenderer(new IceloggedBlockEntityRenderer(null));
        }
    }
}