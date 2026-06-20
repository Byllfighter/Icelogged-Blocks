package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricRegistry implements UniversalRegistry {

    @Override
    public void init(Object bus) {
        // Ignore for Fabric
    }

    @Override
    public <R, T extends R> UniversalRegistryObject<R, T> register(
            Registry<R> registry,
            Identifier id,
            Supplier<T> object
    ) {
        Holder<T> registered = (Holder<T>)Registry.registerForHolder(registry, id, object.get());

        return new FabricRegistryObject<>(registry, registered);
    }

    @Override
    public <I extends Item> UniversalRegistryObject<Item, I> registerItem(
            Identifier id,
            Function<Item.Properties, ? extends I> func,
            Supplier<Item.Properties> properties
    ) {
        Holder<I> item = (Holder<I>) Registry.registerForHolder(BuiltInRegistries.ITEM, id, func.apply(properties.get().setId(ResourceKey.create(Registries.ITEM, id))));

        return new FabricRegistryObject<>(BuiltInRegistries.ITEM, item);
    }

    @Override
    public <B extends Block> UniversalRegistryObject<Block, B> registerBlock(
            Identifier id,
            Function<BlockBehaviour.Properties, ? extends B> func,
            Supplier<BlockBehaviour.Properties> properties
    ) {
        Holder<B> block = (Holder<B>) Registry.registerForHolder(BuiltInRegistries.BLOCK, id, func.apply(properties.get().setId(ResourceKey.create(Registries.BLOCK, id))));

        return new FabricRegistryObject<>(BuiltInRegistries.BLOCK, block);
    }
}