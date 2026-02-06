package com.fish.packaged_faa.mixin.core.overrider;

import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClibanoFrameBlockEntity.class)
public class OverriderClibanoFrame extends BlockEntity {
    public OverriderClibanoFrame(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }
}
