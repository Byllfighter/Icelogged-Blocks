package com.nottouchedgrass.icelogged.payloads;

import com.nottouchedgrass.icelogged.Constants;
import com.nottouchedgrass.icelogged.client.CommonClassClient;
import com.nottouchedgrass.icelogged.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record S2CUpdateBlockEntity(BlockPos pos, CompoundTag data) implements CustomPacketPayload {

    public static final Type<@NotNull S2CUpdateBlockEntity> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Constants.MODID, "s2c_update_block_entity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CUpdateBlockEntity> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, S2CUpdateBlockEntity::pos,
            ByteBufCodecs.COMPOUND_TAG, S2CUpdateBlockEntity::data,
            S2CUpdateBlockEntity::new
    );

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public void execute() {
        if (Services.PLATFORM.isClient()) {
            CommonClassClient.updateBlockEntityPayload(this);
        }
    }
}