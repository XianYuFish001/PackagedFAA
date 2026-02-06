package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.common.registry.block.tile.TileTransmuterUtremJar;
import com.stal111.forbidden_arcanus.common.block.UtremJarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(UtremJarBlock.class)
public class MixinJarTiled implements EntityBlock {
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileTransmuterUtremJar(blockPos, blockState);
    }
}
