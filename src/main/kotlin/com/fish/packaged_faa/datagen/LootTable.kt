package com.fish.packaged_faa.datagen

import com.fish.packaged_faa.common.init.PFAABlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

internal object LootTable {
    internal class Provider(output: PackOutput, provider: CompletableFuture<HolderLookup.Provider>) : LootTableProvider(
        output, setOf(), listOf(
            SubProviderEntry(::Blocks, LootContextParamSets.BLOCK)
        ), provider
    )

    private class Blocks(provider: HolderLookup.Provider) : BlockLootSubProvider(
        setOf(), FeatureFlags.DEFAULT_FLAGS, provider
    ) {
        override fun generate() {
            this.dropSelf(PFAABlocks.pedestalPackaged.get())
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return listOf(
                PFAABlocks.hephaestusPackaged,
                PFAABlocks.pedestalPackaged
            ).map { it.value() }
        }
    }
}