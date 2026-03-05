package com.fish.packaged_faa.mixin.helper

import appeng.api.crafting.IPatternDetails
import appeng.api.stacks.GenericStack
import appeng.crafting.CraftingLink
import appeng.crafting.inv.ListCraftingInventory

interface HelperCraftingJob {
    val tasks: Map<IPatternDetails, HelperJobProgress>
    val waitingFor: ListCraftingInventory
    val link: CraftingLink
    val outputFinal: GenericStack
    val amountRemaining: Long
    val idPlayer: Int?

    interface HelperJobProgress {
        val value: Long
    }
}