package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.common.init.PFAABlocks;
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged;
import com.fish.packaged_faa.integration.impl.jade.ContainerLog;
import com.fish.packaged_faa.mixin.extension.ExtensionManagerRitual;
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ForgeDataCache;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssencesDefinition;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.Ritual;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.RitualResult;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thelm.packagedauto.block.entity.BaseBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@Mixin(RitualManager.class)
public class MixinManagerRitual implements ExtensionManagerRitual {
    @Unique
    private boolean pfaa$packaged = false;
    @Unique
    private Supplier<Logger> pfaa$logger = () -> null;
    @Unique
    private ContainerLog pfaa$containerLog = null;

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
        if (!(instance instanceof UpgradeTierResult(int resultTier)) || resultTier > 0) {
            instance.executeLevelEffect(level, pos);
            return;
        }

        if (!(level.getPlayerByUUID(data.getStartedBy())
                instanceof ServerPlayer player)) return;

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

    @Inject(method = "canStartRitual", at = @At("HEAD"))
    private void logRitual(Ritual ritual, EssencesDefinition definition, CallbackInfoReturnable<Boolean> cir) {
        if (!this.pfaa$packaged) return;
        var logger = this.pfaa$logger.get();
        if (logger == null) return;
        logger.debug("[Check Internal] Matching ritual {}", ritual.toString());
    }

    // 为了打日志只能代劳了(
    @Redirect(method = "canStartRitual",
            at = @At(value = "INVOKE",
                    target = "Lcom/stal111/forbidden_arcanus/common/block/entity/forge/essence/EssencesDefinition;hasMoreThan(Lcom/stal111/forbidden_arcanus/common/block/entity/forge/essence/EssencesDefinition;)Z"),
            require = 0)
    private boolean logEssence(EssencesDefinition instance, EssencesDefinition essencesDefinition) {
        var result = instance.hasMoreThan(essencesDefinition);
        if (result) return true;
        if (!this.pfaa$packaged) return false;


        this.pfaa$containerLog.error("internal", "essence");
        var logger = this.pfaa$logger.get();
        if (logger != null)
            logger.debug("[Check Internal] Insufficient essences");
        return false;
    }

    @Redirect(method = "canStartRitual",
            at = @At(value = "INVOKE",
                    target = "Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ritual/Ritual;canStart(Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ForgeDataCache;I)Z"),
            require = 0)
    private boolean logItem(Ritual instance, ForgeDataCache dataCache, int forgeTier) {
        if (!this.pfaa$packaged) return instance.canStart(dataCache, forgeTier);

        // 这里放在前面是为了优先匹配正确Ritual以免错误Ritual导致log错误错误(
        var resultInput = instance.checkIngredients(dataCache.getIngredients(), dataCache.mainIngredient());
        if (!resultInput) {
//            this.pfaa$containerLog.error("internal", "input");
//            if (this.pfaa$logger != null)
//                this.pfaa$logger.debug("[Check Internal] Wrong or missing input");
            return false;
        }

        var requirement = instance.requirements();

        var resultTier = requirement.tier().test(forgeTier);
        if (!resultTier) {
            this.pfaa$containerLog.error("internal", "tier");
            var logger = this.pfaa$logger.get();
            if (logger != null)
                logger.debug("[Check Internal] Wrong tier");
            return false;
        }

        var resultEnhancer = requirement.enhancers().stream().allMatch(dataCache.getEnhancers()::contains);
        if (!resultEnhancer) {
            this.pfaa$containerLog.error("internal", "enhancer");
            var logger = this.pfaa$logger.get();
            if (logger != null)
                logger.debug("[Check Internal] Missing enhancer");
            return false;
        }
        return true;
    }

    @Override
    public void pfaa$package(boolean value) {
        this.pfaa$packaged = value;
    }

    @Override
    public boolean pfaa$packaged() {
        return this.pfaa$packaged;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void pfaa$bindLogger(ContainerLog loggerJade, Supplier<Logger> loggerDebug) {
        this.pfaa$containerLog = loggerJade;
        this.pfaa$logger = loggerDebug;
    }
}
