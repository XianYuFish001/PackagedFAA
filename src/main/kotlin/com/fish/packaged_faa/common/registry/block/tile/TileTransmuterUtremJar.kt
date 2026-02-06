package com.fish.packaged_faa.common.registry.block.tile

import com.fish.packaged_faa.common.init.PFAATiles
import com.stal111.forbidden_arcanus.core.init.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class TileTransmuterUtremJar(pos: BlockPos, state: BlockState) : BlockEntity(
    PFAATiles.transmuterUtremJar.get(), pos, state
), IFluidHandler {
    private fun transmute() = this.level?.setBlock(
        this.blockPos,
        ModBlocks.ESSENCE_UTREM_JAR.get().defaultBlockState(),
        Block.UPDATE_CLIENTS
    )

    override fun getTanks(): Int {
        this.transmute()
        return 0
    }

    override fun getFluidInTank(p0: Int): FluidStack {
        this.transmute()
        return FluidStack.EMPTY
    }

    override fun getTankCapacity(p0: Int): Int {
        this.transmute()
        return 0
    }

    override fun isFluidValid(p0: Int, p1: FluidStack): Boolean {
        this.transmute()
        return false
    }

    override fun fill(
        p0: FluidStack,
        p1: IFluidHandler.FluidAction
    ): Int {
        this.transmute()
        return 0
    }

    override fun drain(
        p0: FluidStack,
        p1: IFluidHandler.FluidAction
    ): FluidStack {
        this.transmute()
        return FluidStack.EMPTY
    }

    override fun drain(
        p0: Int,
        p1: IFluidHandler.FluidAction
    ): FluidStack {
        this.transmute()
        return FluidStack.EMPTY
    }
}