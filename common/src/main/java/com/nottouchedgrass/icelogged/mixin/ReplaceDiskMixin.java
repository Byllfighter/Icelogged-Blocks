package com.nottouchedgrass.icelogged.mixin;

import com.nottouchedgrass.icelogged.Constants;
import com.nottouchedgrass.icelogged.blockentities.FrostedIceloggedBlockEntity;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ReplaceDisk.class)
public abstract class ReplaceDiskMixin {

    @Mutable
    @Shadow
    @Final
    private Optional<BlockPredicate> predicate;
    @Unique
    boolean _IceloggedBlocks$canBeIcelogged = false;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL"
            )
    )
    private void init(LevelBasedValue radius, LevelBasedValue height, Vec3i offset, Optional<BlockPredicate> allPredicates, BlockStateProvider blockState, Optional triggerGameEvent, CallbackInfo ci) {
        if (blockState instanceof SimpleStateProvider simpleStateProvider) {
            if (((SimpleStateProviderAccessor)simpleStateProvider)._IceloggedBlocks$getBlockState().is(Blocks.FROSTED_ICE)) {
                if (allPredicates.isPresent()) {
                    BlockPredicate blockPredicate = allPredicates.get();
                    try {
                        if (blockPredicate.getClass() == Class.forName("net.minecraft.world.level.levelgen.blockpredicates.AllOfPredicate")) {

                            Field field = Class.forName("net.minecraft.world.level.levelgen.blockpredicates.CombiningPredicate").getDeclaredField("predicates");
                            field.setAccessible(true);

                            List<BlockPredicate> predicates = (List<BlockPredicate>) field.get(blockPredicate);

                            Class<?> matchingFluidsPredicateClass = Class.forName("net.minecraft.world.level.levelgen.blockpredicates.MatchingFluidsPredicate");
                            Field fluidsField = matchingFluidsPredicateClass.getDeclaredField("fluids");
                            fluidsField.setAccessible(true);

                            Class<?> matchingBlocksPredicateClass = Class.forName("net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate");
                            Field blocksField = matchingBlocksPredicateClass.getDeclaredField("blocks");
                            blocksField.setAccessible(true);

                            List<BlockPredicate> newPredicates = new ArrayList<>(predicates);

                            for (BlockPredicate predicate : predicates) {

                                if (predicate.getClass() == matchingFluidsPredicateClass) {
                                    this._IceloggedBlocks$canBeIcelogged = false;
                                    HolderSet<Fluid> fluids = (HolderSet<Fluid>) fluidsField.get(predicate);
                                    if (fluids.size() == 1 && fluids.get(0).value() == Fluids.WATER) {
                                        this._IceloggedBlocks$canBeIcelogged = true;
                                    } else {
                                        break;
                                    }
                                }

                                if (predicate.getClass() == matchingBlocksPredicateClass) {
                                    this._IceloggedBlocks$canBeIcelogged = false;
                                    HolderSet<Block> blocks = (HolderSet<Block>) blocksField.get(predicate);
                                    if (blocks.size() == 1 && blocks.get(0).value() == Blocks.WATER) {
                                        this._IceloggedBlocks$canBeIcelogged = true;
                                        newPredicates.remove(predicate);
                                        newPredicates.add(BlockPredicate.matchesFluids(Fluids.WATER));
                                    } else {
                                        break;
                                    }
                                }
                            }

                            //((ReplaceDiskAccessor) this)._IceloggedBlocks$setPredicate(Optional.of(BlockPredicate.allOf(newPredicates)));
                            this.predicate = Optional.of(BlockPredicate.allOf(newPredicates));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Redirect(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean onPlaceBlock(ServerLevel instance, BlockPos blockPos, BlockState state) {
        boolean isIcelogged = false;
        if (this._IceloggedBlocks$canBeIcelogged) {
            BlockState waterState = instance.getBlockState(blockPos);
            if (waterState.getBlock() instanceof LiquidBlockContainer && !(waterState.getBlock() instanceof LiquidBlock)) {
                BlockState newState = BuiltInRegistries.BLOCK.getValue(Constants.FROSTED_ICELOGGED_BLOCK_ID).defaultBlockState();
                IceloggedBlockEntity blockEntity = new FrostedIceloggedBlockEntity(blockPos, newState);
                blockEntity.innerState = Optional.of(waterState.trySetValue(BlockStateProperties.WATERLOGGED, false));
                BlockEntity waterBlockEntity = instance.getBlockEntity(blockPos);
                if (waterBlockEntity != null) {
                    blockEntity.innerBlockEntity = Optional.of(waterBlockEntity.saveWithFullMetadata(instance.registryAccess()));
                }

                instance.removeBlockEntity(blockPos);
                isIcelogged = instance.setBlockAndUpdate(blockPos, newState);
                if (isIcelogged) {
                    instance.setBlockEntity(blockEntity);
                    return isIcelogged;
                }
            }
        }
        return instance.setBlockAndUpdate(blockPos, state) || isIcelogged;
    }
}