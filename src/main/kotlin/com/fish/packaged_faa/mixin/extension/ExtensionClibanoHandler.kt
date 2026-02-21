package com.fish.packaged_faa.mixin.extension

import com.fish.packaged_faa.common.init.PFAAFluids
import com.fish.packaged_faa.common.registry.fluid.FluidEssence.Companion.toStack
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFireType
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFrameBlockEntity
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoMainBlockEntity
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType
import com.stal111.forbidden_arcanus.common.inventory.clibano.ClibanoMenu
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerHelper
import com.stal111.forbidden_arcanus.core.init.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.inventory.FurnaceFuelSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler

interface ExtensionClibanoHandler {
    val `pfaa$helperTransfer`: HelperHandlerTransfer?

    companion object {
        val ClibanoFrameBlockEntity.helperTransfer: HelperHandlerTransfer?
            get() = (this as? ExtensionClibanoHandler)?.`pfaa$helperTransfer`
    }
}

class HelperHandlerTransfer(private val blockPos: BlockPos, private val level: Level?) {
    private val tileMain by lazy {
        val tiles = Direction.entries
            .map(this.blockPos::relative)
            .map((this.level ?: return@lazy null)::getBlockEntity)
            .filterIsInstance<ClibanoMainBlockEntity>()
        if (tiles.isEmpty()) return@lazy null
        return@lazy tiles[0]
    }

    fun handlerItem(): IItemHandler? {
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

    fun handlerFluid(): IFluidHandler? {
        val tile = this.tileMain ?: return null
        val level = tile.level ?: return null
        return object : IFluidHandler {
            private val amountSoul
                get() = tile.itemStackHandler
                    .getStackInSlot(ClibanoMenu.SOUL_SLOT)
                    .count

            override fun getTanks() = 1

            override fun getFluidInTank(slot: Int): FluidStack {
                if (slot != 0) return FluidStack.EMPTY
                return EssenceType.SOULS.toStack(this.amountSoul)
            }

            override fun getTankCapacity(p0: Int) = 64

            override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
                if (slot != 0) return false
                return stack.fluidType == PFAAFluids.typeSouls.get()
            }

            override fun fill(
                stack: FluidStack,
                action: IFluidHandler.FluidAction
            ): Int {
                if (stack.fluidType != PFAAFluids.typeSouls.get()) return 0

                return stack.amount - tile.itemStackHandler
                    .insertItem(ClibanoMenu.SOUL_SLOT,
                        ItemStack(ModItems.SOUL.get(), stack.amount),
                        action.simulate()
                    ).count
            }

            override fun drain(
                stack: FluidStack,
                action: IFluidHandler.FluidAction
            ): FluidStack {
                if (stack.fluidType != PFAAFluids.typeSouls.get()) return FluidStack.EMPTY
                return this.drain(stack.amount, action)
            }

            override fun drain(
                amount: Int,
                action: IFluidHandler.FluidAction
            ) = EssenceType.SOULS.toStack(
                tile.itemStackHandler
                    .extractItem(ClibanoMenu.SOUL_SLOT,
                        amount,
                        action.simulate())
                    .count
            )
        }
    }
}
