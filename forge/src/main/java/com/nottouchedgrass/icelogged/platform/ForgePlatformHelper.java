package com.nottouchedgrass.icelogged.platform;

import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void sendPayloadToServer(CustomPacketPayload payload) {
        IceloggedMod.CHANNEL.send(payload, Minecraft.getInstance().getConnection().getConnection());
    }

    @Override
    public void sendPayloadToClient(CustomPacketPayload payload, ServerPlayer player) {
        IceloggedMod.CHANNEL.send(payload, player.connection.getConnection());
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}