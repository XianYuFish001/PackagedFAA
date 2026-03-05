package com.fish.packaged_faa.integration.impl.jade

import com.fish.fishlib.integration.jade.InfoBlock
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.PackagedFAA.Companion.location
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.fish.packaged_faa.integration.impl.jade.impl.HephaestusPackaged
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin(PackagedFAA.MODID)
class PFAAJadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        blocks.forEach { it.registerProvider(registration) }
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        blocks.forEach { it.registerConsumer(registration) }
    }

    companion object {
        val blocks = listOf(
            InfoBlock(
                "hephaestus_packaged".location(),
                HephaestusPackaged.Provider::class,
                HephaestusPackaged.Tooltip::class,
                TileHephaestusPackaged::class,
                BlockHephaestusPackaged::class,
            )
        )
    }
}