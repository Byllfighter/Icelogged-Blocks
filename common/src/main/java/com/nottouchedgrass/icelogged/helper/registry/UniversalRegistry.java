package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public interface UniversalRegistry {

    void init(Object bus);

    <R, T extends R> UniversalRegistryObject<R, T> register(Registry<R> registry, Identifier id, Supplier<T> object);

    <I extends Item> UniversalRegistryObject<Item, I> registerItem(Identifier id, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties);
    <B extends Block> UniversalRegistryObject<Block, B> registerBlock(Identifier id, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties);
}
