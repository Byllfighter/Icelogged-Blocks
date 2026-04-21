package com.nottouchedgrass.icelogged.client;

import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntitySpecialRenderer;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CommonClassClient {
    public record BlockEntityUpdateFunctionKey(Level level, BlockPos pos, CompoundTag data) {}
    public static class BlockEntityUpdateFunctionValue {
        public int time;
        public Consumer<BlockEntityUpdateFunctionKey> consumer;
        public BlockEntityUpdateFunctionValue(int time, Consumer<BlockEntityUpdateFunctionKey> consumer) {
            this.time = time;
            this.consumer = consumer;
        }
    }
    public static final Map<BlockEntityUpdateFunctionKey, BlockEntityUpdateFunctionValue> BLOCK_ENTITY_UPDATE = new ConcurrentHashMap<>();

    public static void init() {

    }

    public static void onTick(Minecraft client) {
        //IceloggedMod.LOGGER.info(String.valueOf(IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.size()));
        IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.forEach((key, value) -> value.time -= 1);
        IceloggedBlockEntitySpecialRenderer.CACHED_ICELOGGED_BLOCKENTITIES.values().removeIf((v) -> v.time <= 0);


        Iterator<Map.Entry<BlockEntityUpdateFunctionKey, BlockEntityUpdateFunctionValue>> it = CommonClassClient.BLOCK_ENTITY_UPDATE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<CommonClassClient.BlockEntityUpdateFunctionKey, CommonClassClient.BlockEntityUpdateFunctionValue> entry = it.next();
            CommonClassClient.BlockEntityUpdateFunctionValue value = entry.getValue();

            value.time--;

            if (value.time <= 0) {
                it.remove(); // SAFE removal
                continue;
            }

            value.consumer.accept(entry.getKey());
        }
    }

    public static void updateBlockEntityPayload(S2CUpdateBlockEntity payload) {
        Level initLevel = Minecraft.getInstance().level;
        if (initLevel != null) {
            CommonClassClient.BLOCK_ENTITY_UPDATE.put(new CommonClassClient.BlockEntityUpdateFunctionKey(initLevel, payload.pos(), payload.data()), new CommonClassClient.BlockEntityUpdateFunctionValue(20, key -> {
                Level level = key.level();
                BlockPos pos = key.pos();
                CompoundTag data = key.data();
                if (level == Minecraft.getInstance().level) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data);
                        blockEntity.loadCustomOnly(valueInput);
                        CommonClassClient.BLOCK_ENTITY_UPDATE.remove(key);
                    }
                } else {
                    CommonClassClient.BLOCK_ENTITY_UPDATE.remove(key);
                }
            }));
        }
    }
}
