package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IceloggedMod implements ModInitializer {

    public static final String MODID = "iceloggedblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final Identifier ICELOGGED_BLOCK_ID = Identifier.fromNamespaceAndPath(MODID, "icelogged");
    public static final Block ICELOGGED_BLOCK = Blocks.register(ResourceKey.create(Registries.BLOCK, ICELOGGED_BLOCK_ID), IceloggedBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE));
    public static final Identifier ICELOGGED_BLOCK_ITEM_ID = Identifier.fromNamespaceAndPath(MODID, "icelogged");
    public static final Item ICELOGGED_BLOCK_ITEM = Items.registerBlock(ICELOGGED_BLOCK);
    public static final Identifier ICELOGGED_BLOCK_ENTITY_ID = Identifier.fromNamespaceAndPath(MODID, "icelogged");
    public static final BlockEntityType<IceloggedBlockEntity> ICELOGGED_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ICELOGGED_BLOCK_ENTITY_ID, FabricBlockEntityTypeBuilder.create(IceloggedBlockEntity::new, ICELOGGED_BLOCK).build());


    public static final DataComponentType<IceloggedComponent> ICELOGGED_COMPONENT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(MODID, "icelogged"),
            DataComponentType.<IceloggedComponent>builder().persistent(IceloggedComponent.CODEC).networkSynchronized(IceloggedComponent.STREAM_CODEC).cacheEncoding().build()
    );


    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC);
    }
}
