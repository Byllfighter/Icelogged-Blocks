package com.nottouchedgrass.icelogged.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nottouchedgrass.icelogged.IceloggedMod;
import com.nottouchedgrass.icelogged.blockentities.IceloggedBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class IceloggedComponent implements TooltipProvider {


    public static final IceloggedComponent DEFAULT = new IceloggedComponent(Optional.empty(), Optional.empty());
    public static final Codec<IceloggedComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.optionalFieldOf("innerState").forGetter(IceloggedComponent::getInnerState),
                    CompoundTag.CODEC.optionalFieldOf("innerBlockEntity").forGetter(IceloggedComponent::getInnerBlockEntity)
            ).apply(instance, IceloggedComponent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, IceloggedComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC)), IceloggedComponent::getInnerState,
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG), IceloggedComponent::getInnerBlockEntity,
            IceloggedComponent::new);

    //public static final StreamCodec<RegistryFriendlyByteBuf, SignalComponent> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public Optional<BlockState> innerState;
    public Optional<CompoundTag> innerBlockEntity;

    public IceloggedComponent(Optional<BlockState> innerState, Optional<CompoundTag> innerBlockEntity) {
        this.innerState = innerState;
        this.innerBlockEntity = innerBlockEntity;
    }

    public Optional<BlockState> getInnerState() {
        return innerState;
    }

    public Optional<CompoundTag> getInnerBlockEntity() {
        return innerBlockEntity;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return object instanceof IceloggedComponent iceloggedComponent
                    && this.getInnerState().equals(iceloggedComponent.getInnerState())
                    && this.getInnerBlockEntity().equals(iceloggedComponent.getInnerBlockEntity());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.innerState, this.innerBlockEntity);
    }

    public IceloggedBlockEntity createBlockEntity(Level level, BlockPos pos) {
        IceloggedBlockEntity blockEntity = new IceloggedBlockEntity(pos, IceloggedMod.ICELOGGED_BLOCK.defaultBlockState());
        blockEntity.setLevel(level);
        blockEntity.innerState = this.innerState;
        blockEntity.innerBlockEntity = this.innerBlockEntity;
        return blockEntity;
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
        this.innerState.ifPresent(innerState-> consumer.accept(Component.translatable("block.iceloggedblocks.icelogged.contains", innerState.getBlock().getName()).withStyle(ChatFormatting.GRAY)));
    }
}