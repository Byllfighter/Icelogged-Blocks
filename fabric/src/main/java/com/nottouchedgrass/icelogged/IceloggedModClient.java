package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.client.CommonClassClient;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntityRenderer;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class IceloggedModClient {
    public static void init() {
        BlockEntityRenderers.register(IceloggedMod.ICELOGGED_BLOCK_ENTITY, IceloggedBlockEntityRenderer::new);

        ClientTickEvents.START_CLIENT_TICK.register(CommonClassClient::onTick);

        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateBlockEntity.TYPE, (payload, context) -> {
            payload.execute();
        });
    }
}
