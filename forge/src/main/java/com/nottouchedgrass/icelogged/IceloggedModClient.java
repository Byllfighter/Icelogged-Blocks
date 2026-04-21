package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.client.CommonClassClient;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class IceloggedModClient {
    public static void init(FMLJavaModLoadingContext context) {
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(IceloggedModClient::registerEntityRenderers);
        TickEvent.ClientTickEvent.Post.BUS.addListener(IceloggedModClient::onTick);
    }


    private static void onTick(TickEvent.ClientTickEvent.Post event) {
        CommonClassClient.onTick(Minecraft.getInstance());
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IceloggedMod.ICELOGGED_BLOCK_ENTITY.get(),
                IceloggedBlockEntityRenderer::new
        );
    }

}
