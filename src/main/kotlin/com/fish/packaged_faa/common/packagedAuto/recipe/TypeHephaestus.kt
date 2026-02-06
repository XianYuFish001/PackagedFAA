package com.fish.packaged_faa.common.packagedAuto.recipe

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAAItems
import com.fish.packaged_faa.util.UtilKeyBuilder
import com.fish.packaged_faa.util.flatStack
import com.fish.packaged_faa.util.replace
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.Ritual
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.RitualResult
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.result.UpgradeTierResult
import com.stal111.forbidden_arcanus.common.integration.ForbiddenArcanusJEIPlugin
import com.stal111.forbidden_arcanus.core.init.ModBlocks
import dev.emi.emi.jemi.JemiRecipe
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import thelm.packagedauto.api.IPackageRecipeType
import thelm.packagedauto.api.IRecipeSlotsViewWrapper
import thelm.packagedauto.integration.emi.EmiRecipeWrapper

class TypeHephaestus : IPackageRecipeType {
    override fun getName(): ResourceLocation = id

    override fun getDisplayName(): MutableComponent = TypeHephaestus.name.copy()

    override fun getShortDisplayName(): MutableComponent = nameShort.copy()

    override fun canSetOutput() = true

    override fun isOrdered() = true

    override fun hasCraftingRemainingItem() = false

    override fun getRecipeInfoMapCodec() = InfoHephaestus.mapCodec

    override fun getRecipeInfoCodec() = InfoHephaestus.codec

    override fun getRecipeInfoStreamCodec() = InfoHephaestus.streamCodec

    override fun generateRecipeInfoFromStacks(
        inputs: List<ItemStack>, outputs: List<ItemStack>, level: Level
    ) = InfoHephaestus(
        inputs[slotForge],
        inputs.replace(slotForge, ItemStack.EMPTY),
        outputs[0]
    )

    override fun getEnabledSlots() = slotsAll

    override fun getRepresentation(): ItemStack = ModBlocks.HEPHAESTUS_FORGE_TIER_5.get().asItem().defaultInstance

    override fun getSlotColor(slot: Int): Vec3i {
        return if (slot == slotForge)
            colorSlotForge
        else if (slot == slotOutput)
            colorSlotOutput
        else if (slotsPedestal.contains(slot))
            colorSlotEnabled
        else
            colorSlotDisabled
    }

    override fun getJEICategories(): List<ResourceLocation> {
        return listOf(
            ForbiddenArcanusJEIPlugin.HEPHAESTUS_SMITHING.uid,
            ForbiddenArcanusJEIPlugin.HEPHAESTUS_FORGE_UPGRADING.uid
        )
    }

    override fun getRecipeTransferMap(wrapperRecipe: IRecipeSlotsViewWrapper): Int2ObjectMap<ItemStack> {
        val mapSlots = Int2ObjectOpenHashMap<ItemStack>()
        var recipe: Any
        if (wrapperRecipe is EmiRecipeWrapper) {
            val recipeEmi = wrapperRecipe.recipe()
            if (recipeEmi !is JemiRecipe<*>) return mapSlots
            recipe = recipeEmi.recipe
        } else recipe = wrapperRecipe.recipe
        if (recipe !is Ritual) return mapSlots

        val iteratorPedestal = slotsPedestal.iterator()
        recipe.inputs.map {
            it.ingredient.items[0].copyWithCount(it.amount)
        }.flatStack().forEach { input ->
            if (!iteratorPedestal.hasNext()) return@forEach
            mapSlots.put(iteratorPedestal.nextInt(), input)
        }

        mapSlots.put(slotForge, recipe.mainIngredient.items[0])
        mapSlots.put(slotOutput, recipe.result.getItem(recipe.mainIngredient.items[0]))

        return mapSlots
    }

    private fun RitualResult.getItem(inputMain: ItemStack): ItemStack {
        if (this !is UpgradeTierResult) return this.getResultItem(inputMain)
        else if (this.resultTier <= 0) return PFAAItems.hephaestusPackaged.toStack()
        return HephaestusForgeLevel.getFromIndex(this.resultTier).block.asItem().defaultInstance
    }

    companion object {
        val instance = TypeHephaestus()

        private val id = PackagedFAA.getLocation("hephaestus")
        private val name: MutableComponent
        private val nameShort: MutableComponent

        private val colorSlotForge = Vec3i(162, 139, 139)
        private val colorSlotEnabled = Vec3i(139, 139, 139)
        private val colorSlotDisabled = Vec3i(64, 64, 64)
        private val colorSlotOutput = Vec3i(139, 139, 179)

        /**
         * . . . . . . . . .
         * . . . . P . . . .
         * . . P . . . P . .
         * . . . . . . . . .
         * . P . . F . . P .
         * . . . . . . . . .
         * . . P . . . P . .
         * . . . . P . . . .
         * . . . . . . . . .
         */
        internal val slotsAll: IntSet
        internal val slotsPedestal = IntArraySet.of(13, 20, 24, 37, 43, 56, 60, 67)
        internal val slotForge = 40
        internal val slotOutput = 81

        init {
            val builderName = UtilKeyBuilder.of(UtilKeyBuilder.screenTooltip)
                .addStr("type_recipe")
                .addStr("hephaestus")
            name = builderName.build()
            nameShort = builderName.addStr("short").build()

            slotsAll = IntArraySet(slotsPedestal)
            slotsAll.add(slotForge)
            slotsAll.add(slotOutput)
        }
    }
}