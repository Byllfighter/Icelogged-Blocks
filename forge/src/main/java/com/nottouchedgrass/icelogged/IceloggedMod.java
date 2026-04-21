package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.mixin.BlockPropertiesAccessor;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.payload.PayloadConnection;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

@Mod(value = Constants.MODID)
public class IceloggedMod {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Constants.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Constants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

    public static final RegistryObject<Block> ICELOGGED_BLOCK = BLOCKS.register(Constants.ICELOGGED_BLOCK_ID.getPath(), () -> new IceloggedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).setId(BLOCKS.key(Constants.ICELOGGED_BLOCK_ID.getPath()))));
    public static final RegistryObject<BlockItem> ICELOGGED_BLOCK_ITEM = ITEMS.register(Constants.ICELOGGED_BLOCK_ID.getPath(), () -> new BlockItem(ICELOGGED_BLOCK.get(), new Item.Properties().setId(ITEMS.key(Constants.ICELOGGED_BLOCK_ID.getPath())).useBlockDescriptionPrefix()));
    public static final RegistryObject<BlockEntityType<IceloggedBlockEntity>> ICELOGGED_BLOCK_ENTITY = BLOCK_ENTITIES.register(Constants.ICELOGGED_BLOCK_ENTITY_ID.getPath(), () -> new BlockEntityType<>(IceloggedBlockEntity::new, Set.of(ICELOGGED_BLOCK.get())));

    public static final RegistryObject<DataComponentType<IceloggedComponent>> ICELOGGED_COMPONENT = DATA_COMPONENT_TYPES.register(Constants.ICELOGGED_COMPONENT_ID.getPath(), () -> DataComponentType.<IceloggedComponent>builder().persistent(IceloggedComponent.CODEC).networkSynchronized(IceloggedComponent.STREAM_CODEC).cacheEncoding().build());

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

        BLOCKS.register(busGroup);
        ITEMS.register(busGroup);
        BLOCK_ENTITIES.register(busGroup);
        DATA_COMPONENT_TYPES.register(busGroup);

        CommonClass.init();
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init(context);
        }
    }


}
