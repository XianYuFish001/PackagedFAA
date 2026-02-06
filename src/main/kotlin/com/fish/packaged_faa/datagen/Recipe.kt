package com.fish.packaged_faa.datagen

import com.fish.packaged_faa.common.init.PFAAItems
import com.stal111.forbidden_arcanus.core.init.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import thelm.packagedauto.item.PackagedAutoItems
import java.util.concurrent.CompletableFuture

class Recipe(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PFAAItems.pedestalPackaged.asItem())
            .requires(PackagedAutoItems.PACKAGE_COMPONENT)
            .requires(ModBlocks.DARKSTONE_PEDESTAL.get())
            .unlockedBy("has_component", has(PackagedAutoItems.PACKAGE_COMPONENT))
            .save(recipeOutput)
    }
}