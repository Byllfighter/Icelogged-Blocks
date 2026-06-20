package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.payload.PayloadConnection;

@Mod(value = Constants.MODID)
public class IceloggedMod {

    // Networking
    private static final int PROTOCOL_VERSION = 1;
    public static final PayloadConnection<CustomPacketPayload> PAYLOAD_CONNECTION =
            ChannelBuilder.named(Identifier.fromNamespaceAndPath(Constants.MODID, "main"))
                    .networkProtocolVersion(PROTOCOL_VERSION)
                    .acceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                    .payloadChannel();
    public static Channel<CustomPacketPayload> CHANNEL;

    public IceloggedMod(FMLJavaModLoadingContext context) {
        BusGroup busGroup = context.getModBusGroup();

        PAYLOAD_CONNECTION.play(play -> {
            CHANNEL = play.clientbound()
                    .addMain(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC, (payload, payloadContext) -> payload.execute()).build();
        });

        CommonClass.init(busGroup);
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init(context);
        }
    }


}
