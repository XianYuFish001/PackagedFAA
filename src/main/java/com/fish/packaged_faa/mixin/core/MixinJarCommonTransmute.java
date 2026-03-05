package com.fish.packaged_faa.mixin.core;

import com.stal111.forbidden_arcanus.common.block.UtremJarBlock;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import com.stal111.forbidden_arcanus.common.block.properties.ModBlockStateProperties;
import com.stal111.forbidden_arcanus.core.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UtremJarBlock.class)
public class MixinJarCommonTransmute {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void transmute(ItemStack stack,
                           BlockState state,
                           Level level,
                           BlockPos pos,
                           Player player,
                           InteractionHand hand,
                           BlockHitResult result,
                           CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (!player.isShiftKeyDown()) return;
        if (hand != InteractionHand.MAIN_HAND) return;
        if (!stack.isEmpty()) return;

        var jarEssence = ModBlocks.ESSENCE_UTREM_JAR.get()
                .defaultBlockState()
                .setValue(UtremJarBlock.WATERLOGGED, state.getValue(UtremJarBlock.WATERLOGGED))
                .setValue(ModBlockStateProperties.ESSENCE_TYPE, EssenceType.AUREAL);
        level.setBlockAndUpdate(pos, jarEssence);

        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
}
