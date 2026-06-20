package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeRegistry implements UniversalRegistry {
    public Map<Registry<?>, Map<String, DeferredRegister<?>>> deferredRegisters = new HashMap<>();

    @Override
    public void init(Object bus) {
        for (Map<String, DeferredRegister<?>> value : deferredRegisters.values()) {
            for (DeferredRegister<?> deferredRegister : value.values()) {
                deferredRegister.register((IEventBus) bus);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <R> DeferredRegister<R> getOrCreateRegister(
            Registry<R> registry,
            String namespace
    ) {
        Map<String, DeferredRegister<?>> namespaceMap =
                deferredRegisters.computeIfAbsent(registry, r -> new HashMap<>());

        return (DeferredRegister<R>) namespaceMap.computeIfAbsent(namespace, ns -> {
            if (registry == BuiltInRegistries.ITEM) {
                return DeferredRegister.createItems(ns);
            }

            if (registry == BuiltInRegistries.BLOCK) {
                return DeferredRegister.createBlocks(ns);
            }

            return DeferredRegister.create(registry, ns);
        });
    }

    @Override
    public <R, T extends R> UniversalRegistryObject<R, T> register(
            Registry<R> registry,
            Identifier id,
            Supplier<T> object
    ) {
        DeferredRegister<R> deferredRegister = getOrCreateRegister(registry, id.getNamespace());
        DeferredHolder<R, T> holder = deferredRegister.register(id.getPath(), object);

        return new NeoForgeRegistryObject<>(registry, holder);
    }

    @Override
    public <I extends Item> UniversalRegistryObject<Item, I> registerItem(Identifier id, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
        DeferredRegister.Items deferredRegister = (DeferredRegister.Items) getOrCreateRegister(BuiltInRegistries.ITEM, id.getNamespace());
        DeferredItem<I> holder = deferredRegister.register(id.getPath(), () -> func.apply(properties.get().setId(ResourceKey.create(Registries.ITEM, id))));

        return new NeoForgeRegistryObject<>(BuiltInRegistries.ITEM, holder);
    }

    @Override
    public <B extends Block> UniversalRegistryObject<Block, B> registerBlock(Identifier id, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
        DeferredRegister.Blocks deferredRegister = (DeferredRegister.Blocks) getOrCreateRegister(BuiltInRegistries.BLOCK, id.getNamespace());
        DeferredBlock<B> holder = deferredRegister.register(id.getPath(), () -> func.apply(properties.get().setId(ResourceKey.create(Registries.BLOCK, id))));

        return new NeoForgeRegistryObject<>(BuiltInRegistries.BLOCK, holder);
    }
}
