package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.common.init.PFAADataComponents;
import com.fish.packaged_faa.common.init.PFAAItems;
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualRequirements;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult;
import com.stal111.forbidden_arcanus.common.integration.hephaestus_forge.UpgradeTierCategory;
import com.stal111.forbidden_arcanus.core.init.ModBlocks;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(UpgradeTierCategory.class)
public class MixinCategoryUpgrade {
    @Shadow
    @Final
    private static IntIntPair REQUIRED_TIER_POSITION;
    @Shadow
    @Final
    private static IntIntPair UPGRADED_TIER_POSITION;

    @Inject(method = "buildRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ritual/RitualRequirements;Lcom/stal111/forbidden_arcanus/common/block/entity/forge/ritual/result/UpgradeTierResult;)V",
            at = @At("HEAD"), cancellable = true)
    private void buildRecipe(IRecipeLayoutBuilder builder,
                             RitualRequirements requirements,
                             UpgradeTierResult result,
                             CallbackInfo ci) {
        if (result.resultTier() > 0) return;
        builder.addSlot(RecipeIngredientRole.INPUT,
                REQUIRED_TIER_POSITION.firstInt(),
                REQUIRED_TIER_POSITION.secondInt())
                .addIngredients(VanillaTypes.ITEM_STACK, List.of(
                        ModBlocks.HEPHAESTUS_FORGE_TIER_1.get().asItem().getDefaultInstance(),
                        ModBlocks.HEPHAESTUS_FORGE_TIER_2.get().asItem().getDefaultInstance(),
                        ModBlocks.HEPHAESTUS_FORGE_TIER_3.get().asItem().getDefaultInstance(),
                        ModBlocks.HEPHAESTUS_FORGE_TIER_4.get().asItem().getDefaultInstance(),
                        ModBlocks.HEPHAESTUS_FORGE_TIER_5.get().asItem().getDefaultInstance()
                ));

        var packaged = new ArrayList<ItemStack>(5);
        for (int i = 0; i < 5; i++) {
            var stack = PFAAItems.INSTANCE.getHephaestusPackaged().toStack();
            stack.set(PFAADataComponents.INSTANCE.getLevelForge(),
                    new BlockHephaestusPackaged.DataLevel(HephaestusForgeLevel.getFromIndex(i)));
            packaged.add(stack);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT,
                UPGRADED_TIER_POSITION.firstInt(),
                UPGRADED_TIER_POSITION.secondInt())
                .addIngredients(VanillaTypes.ITEM_STACK, packaged);

        ci.cancel();
    }
}
