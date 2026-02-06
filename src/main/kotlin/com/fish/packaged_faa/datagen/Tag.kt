package com.fish.packaged_faa.datagen

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.common.init.PFAAFluids
import com.fish.packaged_faa.common.registry.PFAATags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.FluidTagsProvider
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

internal object Tag {
    internal class Items(
        output: PackOutput,
        provider: CompletableFuture<HolderLookup.Provider>,
        blocks: CompletableFuture<TagLookup<Block>>,
        helperExisting: ExistingFileHelper
    ) : ItemTagsProvider(output, provider, blocks, PackagedFAA.MODID, helperExisting) {
        override fun addTags(provider: HolderLookup.Provider) {
        }
    }

    internal class Blocks(
        output: PackOutput,
        provider: CompletableFuture<HolderLookup.Provider>,
        helperExisting: ExistingFileHelper
    ) : BlockTagsProvider(output, provider, PackagedFAA.MODID, helperExisting) {
        override fun addTags(provider: HolderLookup.Provider) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                PFAABlocks.hephaestusPackaged.get(),
                PFAABlocks.pedestalPackaged.get()
            )
        }
    }

    internal class Fluids(
        output: PackOutput,
        provider: CompletableFuture<HolderLookup.Provider>,
        helperExisting: ExistingFileHelper
    ) : FluidTagsProvider(output, provider, PackagedFAA.MODID, helperExisting) {
        override fun addTags(provider: HolderLookup.Provider) {
            this.tag(PFAATags.Fluid.aureal).add(PFAAFluids.fluidAureal.first.get())
            this.tag(PFAATags.Fluid.blood).add(PFAAFluids.fluidBlood.first.get())
            this.tag(PFAATags.Fluid.souls).add(PFAAFluids.fluidSouls.first.get())
            this.tag(PFAATags.Fluid.experience).add(PFAAFluids.fluidExperience.first.get())
        }
    }
}