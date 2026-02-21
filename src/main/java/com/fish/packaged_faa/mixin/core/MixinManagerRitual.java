package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.common.init.PFAABlocks;
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged;
import com.fish.packaged_faa.mixin.extension.ExtensionManagerRitual;
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.RitualResult;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thelm.packagedauto.block.entity.BaseBlockEntity;

@Mixin(RitualManager.class)
public class MixinManagerRitual implements ExtensionManagerRitual {
    @Unique
    private boolean pfaa$packaged = false;

    @Inject(method = "finishRitual", at = @At(value = "RETURN"), cancellable = true)
    private void modifyUpgrade(ActiveRitualData data, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.pfaa$packaged) return;
        if (!(data.getRitual().result() instanceof UpgradeTierResult(int resultTier))) return;
        cir.setReturnValue(HephaestusForgeLevel.getFromIndex(resultTier).getBlock().asItem().getDefaultInstance());
    }

    @Redirect(method = "finishRitual",
            at = @At(value = "INVOKE",
                    target = "Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ritual/result/RitualResult;executeLevelEffect(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private void executeUpgrade(RitualResult instance, Level level, BlockPos pos, ActiveRitualData data) {
        if (!(instance instanceof UpgradeTierResult(int resultTier))) {
            instance.executeLevelEffect(level, pos);
            return;
        }

        if (!(level.getPlayerByUUID(data.getStartedBy())
                instanceof ServerPlayer player)) return;

        if (resultTier > 0) return;

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

        if (level.getBlockEntity(pos) instanceof BaseBlockEntity tileBase)
            tileBase.setOwner(player);
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
