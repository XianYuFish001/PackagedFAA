package com.fish.packaged_faa.common.registry.fluid

import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.neoforged.neoforge.fluids.BaseFlowingFluid

open class FluidVirtual(private val source: Boolean, properties: Properties) : BaseFlowingFluid(properties) {
    override fun getSource(): Fluid {
        return if (this.source) this
        else super.getSource()
    }

    override fun getFlowing(): Fluid {
        return if (this.source) super.getFlowing()
        else this
    }

    override fun getBucket(): Item = Items.AIR

    override fun createLegacyBlock(state: FluidState): BlockState = Blocks.AIR.defaultBlockState()

    override fun getAmount(p0: FluidState) = 0

    override fun isSource(p0: FluidState) = this.source
}