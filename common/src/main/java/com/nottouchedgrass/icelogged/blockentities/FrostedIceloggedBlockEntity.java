package com.nottouchedgrass.icelogged.blockentities;

import com.nottouchedgrass.icelogged.CommonClass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FrostedIceloggedBlockEntity extends IceloggedBlockEntity {

    public FrostedIceloggedBlockEntity(BlockPos pos, BlockState state) {
        super(CommonClass.FROSTED_ICELOGGED_BLOCK_ENTITY.get(), pos, state);
    }
}
