package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.mixin.core.overrider.OverriderBlockJarEssence;
import com.fish.packaged_faa.mixin.extension.ExtensionJarEssence;
import com.stal111.forbidden_arcanus.common.block.EssenceUtremJarBlock;
import com.stal111.forbidden_arcanus.common.block.entity.EssenceUtremJarBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import com.stal111.forbidden_arcanus.common.block.properties.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EssenceUtremJarBlock.class)
public class MixinBlockJarEssence {
    @Inject(method = "getTicker", at = @At("HEAD"), cancellable = true)
    private void getTicker(Level var1,
                           BlockState var2,
                           BlockEntityType<BlockEntity> blockEntityType,
                           CallbackInfoReturnable<BlockEntityTicker<BlockEntity>> cir) {
        if (var1.isClientSide()) return;
        cir.setReturnValue((level, pos, state, tile) -> {
            if (!(tile instanceof ExtensionJarEssence extension)) return;
            extension.pfaa$tickServer((ServerLevel) level, pos, state);
        });
    }

    @Dynamic(mixin = OverriderBlockJarEssence.class)
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void onInteract(BlockState state,
                            Level level,
                            BlockPos pos,
                            Player player,
                            BlockHitResult hitResult,
                            CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown()) return;

        var tile = level.getBlockEntity(pos);
        if (!(tile instanceof EssenceUtremJarBlockEntity tileJar)) return;
        if (tileJar.getAmount() > 0) return;

        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        var type = state.getValue(ModBlockStateProperties.ESSENCE_TYPE);
        var types = EssenceType.class.getEnumConstants();
        type = types[(type.ordinal() + 1) % types.length];
        level.setBlock(pos, state.setValue(ModBlockStateProperties.ESSENCE_TYPE, type), EssenceUtremJarBlock.UPDATE_CLIENTS);
        cir.setReturnValue(InteractionResult.CONSUME);
    }
}
