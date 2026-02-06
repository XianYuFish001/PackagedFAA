package com.fish.packaged_faa.mixin.extension

import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFireType
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFrameBlockEntity
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoMainBlockEntity
import com.stal111.forbidden_arcanus.common.inventory.clibano.ClibanoMenu
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.inventory.FurnaceFuelSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.IItemHandler

interface ExtensionClibanoHandler {
    val `pfaa$helperTransfer`: HelperHandlerTransfer?

    companion object {
        val ClibanoFrameBlockEntity.helperTransfer: HelperHandlerTransfer?
            get() = (this as ExtensionClibanoHandler).`pfaa$helperTransfer`
    }

    class HelperHandlerTransfer(private val blockPos: BlockPos, private val level: Level?) {
        private var tileMain: ClibanoMainBlockEntity? = null

        fun handlerItem(): IItemHandler? {
            if (this.tileMain == null) {
                val tiles = Direction.entries
                    .map(this.blockPos::relative)
                    .map((this.level ?: return null)::getBlockEntity)
                    .filterIsInstance<ClibanoMainBlockEntity>()
                if (tiles.isEmpty()) return null
                this.tileMain = tiles[0]
            }
            val tile = this.tileMain ?: return null
            val level = tile.level ?: return null
            return object : IItemHandler {
                override fun getSlots() = ClibanoMenu.SLOT_COUNT - 2

                override fun getStackInSlot(slot: Int) = tile.getStack(slot)

                override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                    val slotTarget = when {
                        EnhancerHelper.getEnhancerHolder(level.registryAccess(), stack).isPresent ->
                            ClibanoMenu.ENHANCER_SLOT

                        ClibanoFireType.fromItem(stack) != ClibanoFireType.FIRE ->
                            ClibanoMenu.SOUL_SLOT

                        stack.getBurnTime(RecipeType.BLASTING) > 0 || FurnaceFuelSlot.isBucket(stack) ->
                            ClibanoMenu.FUEL_SLOT

                        slot in 3..4 -> slot

                        slot in 5..6 -> -1

                        else -> -1
                    }
                    if (slotTarget == -1) return stack
                    return tile.itemStackHandler.insertItem(slotTarget, stack, simulate)
                }

                override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
                    val slotTarget = if (slot in 0..1) slot + 5 else return ItemStack.EMPTY
                    return tile.itemStackHandler.extractItem(slotTarget, amount, simulate)
                }

                override fun getSlotLimit(p0: Int) = 64

                override fun isItemValid(slot: Int, stack: ItemStack) = when (slot) {
                    ClibanoMenu.ENHANCER_SLOT ->
                        EnhancerHelper.getEnhancerHolder(level.registryAccess(), stack) != null

                    ClibanoMenu.SOUL_SLOT ->
                        ClibanoFireType.fromItem(stack) != ClibanoFireType.FIRE

                    ClibanoMenu.FUEL_SLOT ->
                        stack.getBurnTime(RecipeType.BLASTING) > 0 || FurnaceFuelSlot.isBucket(stack)

                    in 3..4 -> true

                    else -> false
                }
            }
        }
    }
}