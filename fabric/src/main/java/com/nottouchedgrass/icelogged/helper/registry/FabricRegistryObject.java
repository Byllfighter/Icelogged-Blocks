package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class FabricRegistryObject<R, T extends R> implements UniversalRegistryObject<R, T> {

    public final Registry<R> registry;
    public final Holder<T> holder;

    FabricRegistryObject(Registry<R> registry, Holder<T> holder) {
        this.registry = registry;
        this.holder = holder;
    }

    @Override
    public Registry<R> registry() {
        return this.registry;
    }

    public Supplier<T> supplier() {
        return holder::value;
    }

    @Override
    public Object loaderSpecific() {
        return null;
    }

    @Override
    public Holder<T> holder() {
        return this.holder;
    }

    @Override
    public T get() {
        return holder.value();
    }
}
