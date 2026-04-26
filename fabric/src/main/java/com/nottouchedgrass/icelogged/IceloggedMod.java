package com.nottouchedgrass.icelogged;

import com.nottouchedgrass.icelogged.blockentities.FrostedIceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blocks.FrostedIceloggedBlock;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import com.nottouchedgrass.icelogged.mixin.ItemsAccessor;
import com.nottouchedgrass.icelogged.payloads.S2CUpdateBlockEntity;
import com.nottouchedgrass.icelogged.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class IceloggedMod implements ModInitializer {

    public static final Block ICELOGGED_BLOCK = Blocks.register(ResourceKey.create(Registries.BLOCK, Constants.ICELOGGED_BLOCK_ID), IceloggedBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE));
    public static final Item ICELOGGED_BLOCK_ITEM = ItemsAccessor._IceloggedBlocks$invokeRegisterBlock(ICELOGGED_BLOCK);
    public static final BlockEntityType<IceloggedBlockEntity> ICELOGGED_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.ICELOGGED_BLOCK_ENTITY_ID, FabricBlockEntityTypeBuilder.create(IceloggedBlockEntity::new, ICELOGGED_BLOCK).build());

    public static final Block FROSTED_ICELOGGED_BLOCK = Blocks.register(ResourceKey.create(Registries.BLOCK, Constants.FROSTED_ICELOGGED_BLOCK_ID), FrostedIceloggedBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.ICE));
    public static final BlockEntityType<FrostedIceloggedBlockEntity> FROSTED_ICELOGGED_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.FROSTED_ICELOGGED_BLOCK_ID, FabricBlockEntityTypeBuilder.create(FrostedIceloggedBlockEntity::new, FROSTED_ICELOGGED_BLOCK).build());

    public static final DataComponentType<IceloggedComponent> ICELOGGED_COMPONENT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Constants.ICELOGGED_COMPONENT_ID,
            DataComponentType.<IceloggedComponent>builder().persistent(IceloggedComponent.CODEC).networkSynchronized(IceloggedComponent.STREAM_CODEC).cacheEncoding().build()
    );

    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        PayloadTypeRegistry.clientboundPlay().register(S2CUpdateBlockEntity.TYPE, S2CUpdateBlockEntity.STREAM_CODEC);

        CommonClass.init();
        if (Services.PLATFORM.isClient()) {
            IceloggedModClient.init();
        }
    }
}
