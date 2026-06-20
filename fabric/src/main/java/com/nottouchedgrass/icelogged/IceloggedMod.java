package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class IceloggedMod implements ModInitializer {

    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        PayloadTypeRegistry.clientboundPlay().register(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC);

        CommonClass.init(null);
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init();
        }
    }
}
