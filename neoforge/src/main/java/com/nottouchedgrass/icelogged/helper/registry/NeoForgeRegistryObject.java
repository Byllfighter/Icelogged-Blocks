package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class NeoForgeRegistryObject<R, T extends R> implements UniversalRegistryObject<R, T> {

    public final Registry<R> registry;
    public final DeferredHolder<R, T> deferredHolder;

    NeoForgeRegistryObject(Registry<R> registry, DeferredHolder<R, T> deferredHolder) {
        this.registry = registry;
        this.deferredHolder = deferredHolder;
    }

    @Override
    public Registry<R> registry() {
        return this.registry;
    }

    public Supplier<T> supplier() {
        return this.deferredHolder::value;
    }

    @Override
    public Object loaderSpecific() {
        return this.deferredHolder;
    }

    public DeferredHolder<R, T> deferredHolder() {
        return this.deferredHolder;
    }

    @Override
    public Holder<T> holder() {
        return (Holder<T>) this.deferredHolder.getDelegate();
    }

    @Override
    public T get() {
        return this.deferredHolder.get();
    }
}
