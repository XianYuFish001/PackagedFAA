package com.fish.packaged_faa.mixin.core.overrider;

import com.stal111.forbidden_arcanus.common.block.EssenceUtremJarBlock;
import com.stal111.forbidden_arcanus.common.block.UtremJarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EssenceUtremJarBlock.class)
public class OverriderBlockJarEssence extends UtremJarBlock {
    public OverriderBlockJarEssence(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
