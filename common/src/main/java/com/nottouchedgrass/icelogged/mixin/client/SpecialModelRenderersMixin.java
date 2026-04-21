package com.nottouchedgrass.icelogged.mixin.client;

import com.mojang.serialization.MapCodec;
import com.nottouchedgrass.icelogged.Constants;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntitySpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {
    @Shadow
    @Final
    public static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> ID_MAPPER;

    @Inject(
            method = "bootstrap",
            at = @At("TAIL")
    )
    private static void shouldRenderFaceMixin(
            CallbackInfo ci
    ) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath(Constants.MODID, "icelogged_special_renderer"), IceloggedBlockEntitySpecialRenderer.Unbaked.MAP_CODEC);
    }
}
