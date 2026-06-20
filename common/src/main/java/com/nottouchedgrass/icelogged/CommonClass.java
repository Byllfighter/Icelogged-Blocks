package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.blockentities.FrostedIceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blocks.FrostedIceloggedBlock;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.client.CommonClassClient;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.helper.registry.UniversalRegistry;
import com.nottouchedgrass.icelogged.helper.registry.UniversalRegistryObject;
import com.nottouchedgrass.icelogged.mixin.BlockPropertiesAccessor;
import com.nottouchedgrass.icelogged.platform.Services;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as NeoForge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {

    public static UniversalRegistry REGISTRY = Services.PLATFORM.createUniversalRegistry();

    public static final UniversalRegistryObject<Block, Block> ICELOGGED_BLOCK = REGISTRY.registerBlock(Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged"), IceloggedBlock::new, ()->BlockBehaviour.Properties.ofFullCopy(Blocks.ICE));
    public static final UniversalRegistryObject<Item, BlockItem> ICELOGGED_BLOCK_ITEM = REGISTRY.registerItem(Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged"), properties -> new BlockItem(ICELOGGED_BLOCK.get(), properties), ()->new Item.Properties().useBlockDescriptionPrefix());
    public static final UniversalRegistryObject<BlockEntityType<?>, BlockEntityType<IceloggedBlockEntity>> ICELOGGED_BLOCK_ENTITY = REGISTRY.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged"), () -> new BlockEntityType<>(IceloggedBlockEntity::new, Set.of(ICELOGGED_BLOCK.get())));

    public static final UniversalRegistryObject<DataComponentType<?>, DataComponentType<IceloggedComponent>> ICELOGGED_COMPONENT = REGISTRY.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged"), () -> DataComponentType.<IceloggedComponent>builder().persistent(IceloggedComponent.CODEC).networkSynchronized(IceloggedComponent.STREAM_CODEC).cacheEncoding().build());

    public static final UniversalRegistryObject<Block, Block> FROSTED_ICELOGGED_BLOCK = REGISTRY.registerBlock(Identifier.fromNamespaceAndPath(Constants.MODID, "frosted_icelogged"), FrostedIceloggedBlock::new, ()->BlockBehaviour.Properties.ofFullCopy(Blocks.ICE));
    public static final UniversalRegistryObject<BlockEntityType<?>, BlockEntityType<FrostedIceloggedBlockEntity>> FROSTED_ICELOGGED_BLOCK_ENTITY = REGISTRY.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MODID, "frosted_icelogged"), () -> new BlockEntityType<>(FrostedIceloggedBlockEntity::new, Set.of(FROSTED_ICELOGGED_BLOCK.get())));


    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init(Object bus) {
        REGISTRY.init(bus);
        if (Services.PLATFORM.isClient()) {
            CommonClassClient.init();
        }
    }
}