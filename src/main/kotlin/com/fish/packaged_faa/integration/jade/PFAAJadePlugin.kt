package com.fish.packaged_faa.integration.jade

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.fish.packaged_faa.integration.jade.helper.IObjectedProvider
import com.fish.packaged_faa.integration.jade.helper.WrapperObjectedProvider
import com.fish.packaged_faa.integration.jade.impl.HephaestusPackaged
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import snownee.jade.api.*

@WailaPlugin(PackagedFAA.MODID)
class PFAAJadePlugin : IWailaPlugin {
    val blocks: List<InfoBlock<*, *>> = listOf(
        InfoBlock(
            "hephaestus_packaged",
            HephaestusPackaged.Provider::class.java,
            HephaestusPackaged.Tooltip::class.java,
            TileHephaestusPackaged::class.java,
            BlockHephaestusPackaged::class.java,
        )
    )

    override fun register(registration: IWailaCommonRegistration) =
        this.blocks.forEach { it.registerProvider(registration) }

    override fun registerClient(registration: IWailaClientRegistration) =
        this.blocks.forEach { it.registerConsumer(registration) }

    @JvmRecord
    data class InfoBlock<TProvider, TConsumer>(
        val uid: String,
        val clazzProvider: Class<TProvider>,
        val clazzConsumer: Class<TConsumer>,
        val clazzBlockEntity: Class<out BlockEntity>,
        val clazzBlock: Class<out Block>
    ) where TProvider : Enum<TProvider>,
            TProvider : IObjectedProvider<BlockAccessor>,
            TConsumer : Enum<TConsumer>,
            TConsumer : IBlockComponentProvider {
        fun registerProvider(registration: IWailaCommonRegistration) {
            registration.registerBlockDataProvider(
                WrapperObjectedProvider.create(this.uid, this.clazzProvider),
                this.clazzBlockEntity
            )
        }

        fun registerConsumer(registration: IWailaClientRegistration) {
            for (entry in this.clazzConsumer.getEnumConstants()) {
                registration.registerBlockComponent(entry, this.clazzBlock)
            }
        }
    }
}