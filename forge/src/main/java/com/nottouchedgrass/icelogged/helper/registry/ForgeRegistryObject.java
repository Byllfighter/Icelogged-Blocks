package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ForgeRegistryObject<R, T extends R> implements UniversalRegistryObject<R, T> {

    public final Registry<R> registry;
    public final RegistryObject<T> registryObject;

    ForgeRegistryObject(Registry<R> registry, RegistryObject<T> registryObject) {
        this.registry = registry;
        this.registryObject = registryObject;
    }

    @Override
    public Registry<R> registry() {
        return this.registry;
    }

    public Supplier<T> supplier() {
        return this.registryObject::get;
    }

    @Override
    public Object loaderSpecific() {
        return this.registryObject;
    }

    public RegistryObject<T> deferredHolder() {
        return this.registryObject;
    }

    @Override
    public Holder<T> holder() {
        return this.registryObject.getHolder().orElse(null);
    }

    @Override
    public T get() {
        return this.registryObject.get();
    }
}
