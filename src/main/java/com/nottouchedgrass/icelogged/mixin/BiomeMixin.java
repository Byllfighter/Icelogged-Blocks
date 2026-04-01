package com.nottouchedgrass.icelogged.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public abstract class BiomeMixin {

    @Definition(id = "blockState", local = @Local(type = BlockState.class))
    @Definition(id = "getBlock", method = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
    @Definition(id = "LiquidBlock", type = LiquidBlock.class)
    @Expression("blockState.getBlock() instanceof LiquidBlock")
    @ModifyExpressionValue(
            method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION"
            )
    )
    private boolean allowLiquidContainers(
            boolean original,
            @Local(name = "blockState") BlockState blockState
    ) {
        return original || blockState.getBlock() instanceof LiquidBlockContainer;
    }
}
