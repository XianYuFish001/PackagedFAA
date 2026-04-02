package com.fish.packaged_faa.mixin.core;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import net.minecraft.util.ExtraCodecs;
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

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.intRange(-1, 5).fieldOf("result_tier").forGetter(UpgradeTierResult::resultTier)
        ).apply(instance, UpgradeTierResult::new));
    }
}
