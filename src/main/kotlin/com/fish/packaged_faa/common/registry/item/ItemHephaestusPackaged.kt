package com.fish.packaged_faa.common.registry.item

import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.common.init.PFAADataComponents
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.BlockState

class ItemHephaestusPackaged : BlockItem(
    PFAABlocks.hephaestusPackaged.get(),
    Properties().component(
        PFAADataComponents.levelForge,
        BlockHephaestusPackaged.DataLevel(HephaestusForgeLevel.ONE)
    )
) {
    override fun getPlacementState(context: BlockPlaceContext): BlockState? {
        val state = super.getPlacementState(context)
        val level = context.itemInHand.getOrDefault(
            PFAADataComponents.levelForge,
            BlockHephaestusPackaged.DataLevel(HephaestusForgeLevel.ONE)
        ).level.asInt

        return state?.setValue(BlockHephaestusPackaged.propertyLevelForge, level)
    }
}