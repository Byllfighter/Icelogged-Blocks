package com.nottouchedgrass.icelogged.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public interface UniversalRegistryObject<R, T extends R> {

    Registry<R> registry();
    Supplier<T> supplier();
    Object loaderSpecific();
    Holder<T> holder();

    default T get() {
        return this.supplier().get();
    }
}
