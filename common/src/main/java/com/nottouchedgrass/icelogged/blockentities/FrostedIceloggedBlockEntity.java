package com.nottouchedgrass.icelogged.blockentities;

import com.nottouchedgrass.icelogged.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public class FrostedIceloggedBlockEntity extends IceloggedBlockEntity {

    public FrostedIceloggedBlockEntity(BlockPos pos, BlockState state) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Constants.FROSTED_ICELOGGED_BLOCK_ENTITY_ID), pos, state);
    }
}
