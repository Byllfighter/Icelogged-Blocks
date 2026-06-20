package com.nottouchedgrass.icelogged;


import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MODID)
public class IceloggedMod {

    public IceloggedMod(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        eventBus.addListener(this::registerPayloads);

        CommonClass.init(eventBus);
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init(eventBus);
        }

    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC, ((payload, context) -> payload.execute()));
    }
}