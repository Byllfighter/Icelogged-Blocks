package com.nottouchedgrass.icelogged.blockentities;

import com.nottouchedgrass.icelogged.Constants;
import com.nottouchedgrass.icelogged.blocks.IceloggedBlock;
import com.nottouchedgrass.icelogged.components.IceloggedComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class IceloggedBlockEntity extends BlockEntity {

    public Optional<BlockState> innerState;
    public Optional<CompoundTag> innerBlockEntity;

    public IceloggedBlockEntity(BlockPos pos, BlockState state) {
        this(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Constants.ICELOGGED_BLOCK_ENTITY_ID), pos, state);
    }

    public IceloggedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.innerState = Optional.empty();
        this.innerBlockEntity = Optional.empty();
        //this.innerState = Optional.of(Blocks.ACACIA_PLANKS.defaultBlockState());
    }

    protected void loadAdditional(@NotNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        innerState = valueInput.read("innerState", BlockState.CODEC);
        innerBlockEntity = valueInput.read("innerBlockEntity", CompoundTag.CODEC);
    }

    protected void saveAdditional(@NotNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        innerState.ifPresent(state -> valueOutput.store("innerState", BlockState.CODEC, state));
        innerBlockEntity.ifPresent(tag -> valueOutput.store("innerBlockEntity", CompoundTag.CODEC, tag));
    }

    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        TagValueOutput valueOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        this.saveAdditional(valueOutput);
        tag.merge(valueOutput.buildResult());
        return tag;
    }

    public void setChanged( Holder.@Nullable Reference<GameEvent> reference) {
        super.setChanged();
        if (this.level != null) {
            if (reference != null) {
                this.level.gameEvent(reference, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
            }
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), IceloggedBlock.UPDATE_ALL);
        }
    }

    @Override
    public void setChanged() {
        this.setChanged(GameEvent.BLOCK_CHANGE);
    }


    @Override
    protected void applyImplicitComponents(DataComponentGetter dataComponentGetter) {
        super.applyImplicitComponents(dataComponentGetter);
        IceloggedComponent component = dataComponentGetter.getOrDefault((DataComponentType<IceloggedComponent>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Constants.ICELOGGED_COMPONENT_ID), IceloggedComponent.DEFAULT);
        this.innerState = component.innerState;
        this.innerBlockEntity = component.innerBlockEntity;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set((DataComponentType<IceloggedComponent>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Constants.ICELOGGED_COMPONENT_ID), new IceloggedComponent(innerState, innerBlockEntity));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        valueOutput.discard("innerState");
        valueOutput.discard("innerBlockEntity");
    }


}
