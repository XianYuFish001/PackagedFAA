package com.fish.packaged_faa.common.init

import com.fish.fishlib.common.InitObject
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.fish.packaged_faa.common.registry.block.BlockPedestalPackaged
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.MapColor
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object PFAABlocks {
    @InitObject
    val register: DeferredRegister.Blocks = DeferredRegister.Blocks.createBlocks(PackagedFAA.MODID)

    val hephaestusPackaged: DeferredBlock<BlockHephaestusPackaged> = register.registerBlock(
        "hephaestus_packaged", ::BlockHephaestusPackaged, this.propStone().dynamicShape().noLootTable())
    val pedestalPackaged: DeferredBlock<BlockPedestalPackaged> = register.registerBlock(
        "pedestal_packaged", ::BlockPedestalPackaged, this.propStone().dynamicShape())

    private fun propStone() = Properties.of()
        .strength(2.2f, 11f)
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .forceSolidOn()
}