package com.nottouchedgrass.icelogged.client.mixin;

import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.client.renderers.IceloggedBlockEntitySpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public class SpecialModelRenderersMixin {
    @Inject(
            method = "bootstrap",
            at = @At("TAIL")
    )
    private static void shouldRenderFaceMixin(
            CallbackInfo ci
    ) {
        SpecialModelRenderers.ID_MAPPER.put(Identifier.fromNamespaceAndPath(IceloggedMod.MODID, "icelogged_special_renderer"), IceloggedBlockEntitySpecialRenderer.Unbaked.MAP_CODEC);
    }
}
