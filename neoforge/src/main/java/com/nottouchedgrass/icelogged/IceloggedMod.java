package com.nottouchedgrass.icelogged;


import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.mixin.BlockPropertiesAccessor;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

@Mod(Constants.MODID)
public class IceloggedMod {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

    public static final DeferredBlock<Block> ICELOGGED_BLOCK = BLOCKS.registerBlock(Constants.ICELOGGED_BLOCK_ID.getPath(), properties -> new IceloggedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).setId(((BlockPropertiesAccessor)properties)._IceloggedBlocks$getId())));
    public static final DeferredItem<BlockItem> ICELOGGED_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(Constants.ICELOGGED_BLOCK_ID.getPath(), ICELOGGED_BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IceloggedBlockEntity>> ICELOGGED_BLOCK_ENTITY = BLOCK_ENTITIES.register(Constants.ICELOGGED_BLOCK_ENTITY_ID.getPath(), () -> new BlockEntityType<>(IceloggedBlockEntity::new, Set.of(ICELOGGED_BLOCK.get())));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IceloggedComponent>> ICELOGGED_COMPONENT = DATA_COMPONENT_TYPES.register(Constants.ICELOGGED_COMPONENT_ID.getPath(), () -> DataComponentType.<IceloggedComponent>builder().persistent(IceloggedComponent.CODEC).networkSynchronized(IceloggedComponent.STREAM_CODEC).cacheEncoding().build());


    public IceloggedMod(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        eventBus.addListener(this::registerPayloads);

        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        DATA_COMPONENT_TYPES.register(eventBus);

        CommonClass.init();
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init(eventBus);
        }

    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC, ((payload, context) -> payload.execute()));
    }
}