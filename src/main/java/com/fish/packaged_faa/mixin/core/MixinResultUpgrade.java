package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.common.init.PFAABlocks;
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpgradeTierResult.class)
public class MixinResultUpgrade {
    @Shadow
    @Final
    @Mutable
    public static MapCodec<UpgradeTierResult> CODEC;

    @Shadow
    @Final
    private int resultTier;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.intRange(-1, 5).fieldOf("result_tier").forGetter(UpgradeTierResult::resultTier)
        ).apply(instance, UpgradeTierResult::new));
    }

    @Inject(method = "executeLevelEffect", at = @At("HEAD"), cancellable = true)
    private void executeLevelEffect(Level level, BlockPos pos, CallbackInfo ci) {
        if (this.resultTier > 0) return;

        var state = level.getBlockState(pos);
        var block = state.getBlock();
        if (!(block instanceof HephaestusForgeBlock blockForge)) return;

        if (level.getBlockEntity(pos) instanceof HephaestusForgeBlockEntity tile)
            tile.getItemStackHandler().extractItem(4, 1, false).setCount(0);

        var levelForge = blockForge.getLevel().getAsInt();
        var packaged = PFAABlocks.INSTANCE.getHephaestusPackaged().get()
                .defaultBlockState()
                .setValue(BlockHephaestusPackaged.Companion.getPropertyLevelForge(), levelForge);
        level.setBlockAndUpdate(pos, packaged);
        ci.cancel();
    }
}
