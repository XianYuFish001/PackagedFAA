package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.mixin.extension.ExtensionManagerRitual;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RitualManager.class)
public class MixinManagerRitual implements ExtensionManagerRitual {
    @Unique
    private boolean pfaa$packaged = false;

    @Inject(method = "finishRitual",
            at = @At(value = "INVOKE",
                    target = "Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ritual/result/RitualResult;executeLevelEffect(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"),
            cancellable = true)
    private void modifyUpgrade(ActiveRitualData data, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.pfaa$packaged) return;
        if (!(data.getRitual().result() instanceof UpgradeTierResult(int resultTier))) return;
        cir.setReturnValue(HephaestusForgeLevel.getFromIndex(resultTier).getBlock().asItem().getDefaultInstance());
    }

    @Override
    public void pfaa$package(boolean value) {
        this.pfaa$packaged = value;
    }

    @Override
    public boolean pfaa$packaged() {
        return this.pfaa$packaged;
    }
}
