package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.client.CommonClassClient;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

public class IceloggedModClient {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(IceloggedModClient::registerEntityRenderers);
        NeoForge.EVENT_BUS.addListener(IceloggedModClient::onTick);
    }


    private static void onTick(ClientTickEvent.Post event) {
        CommonClassClient.onTick(Minecraft.getInstance());
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IceloggedMod.ICELOGGED_BLOCK_ENTITY.get(), IceloggedBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(IceloggedMod.FROSTED_ICELOGGED_BLOCK_ENTITY.get(), IceloggedBlockEntityRenderer::new);
    }

}
