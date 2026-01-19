package com.nottouchedgrass.icelogged.client;

import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntityRenderer;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntitySpecialRenderer;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class IceloggedModClient implements ClientModInitializer {

    public record BlockEntityUpdateFunctionKey(Level level, BlockPos pos, CompoundTag data) {}
    public static class BlockEntityUpdateFunctionValue {
        public int time;
        public Consumer<BlockEntityUpdateFunctionKey> consumer;
        BlockEntityUpdateFunctionValue(int time, Consumer<BlockEntityUpdateFunctionKey> consumer) {
            this.time = time;
            this.consumer = consumer;
        }
    }
    public static final Map<BlockEntityUpdateFunctionKey, BlockEntityUpdateFunctionValue> BLOCK_ENTITY_UPDATE = new ConcurrentHashMap<>();

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(
                IceloggedMod.ICELOGGED_BLOCK,
                ChunkSectionLayer.TRANSLUCENT
        );
        BlockEntityRenderers.register(IceloggedMod.ICELOGGED_BLOCK_ENTITY, IceloggedBlockEntityRenderer::new);
        SpecialBlockRendererRegistry.register(IceloggedMod.ICELOGGED_BLOCK, new IceloggedBlockEntitySpecialRenderer.Unbaked());

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            //IceloggedMod.LOGGER.info(String.valueOf(IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.size()));
            IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.forEach((key, value) -> value.time -= 1);
            IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.values().removeIf((v) -> v.time <= 0);


            Iterator<Map.Entry<BlockEntityUpdateFunctionKey, BlockEntityUpdateFunctionValue>> it = BLOCK_ENTITY_UPDATE.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BlockEntityUpdateFunctionKey, BlockEntityUpdateFunctionValue> entry = it.next();
                BlockEntityUpdateFunctionValue value = entry.getValue();

                value.time--;

                if (value.time <= 0) {
                    it.remove(); // SAFE removal
                    continue;
                }

                value.consumer.accept(entry.getKey());
            }
        });


        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateBlockEntity.TYPE, (payload, context) -> {
            Level initLevel = context.client().level;
            if (initLevel != null) {
                BLOCK_ENTITY_UPDATE.put(new BlockEntityUpdateFunctionKey(initLevel, payload.pos(), payload.data()), new BlockEntityUpdateFunctionValue(20, key -> {
                    Level level = key.level;
                    BlockPos pos = key.pos;
                    CompoundTag data = key.data;
                    if (level == Minecraft.getInstance().level) {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity != null) {
                            ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data);
                            blockEntity.loadCustomOnly(valueInput);
                            BLOCK_ENTITY_UPDATE.remove(key);
                        }
                    } else {
                        BLOCK_ENTITY_UPDATE.remove(key);
                    }
                }));
            }
        });
    }
}
