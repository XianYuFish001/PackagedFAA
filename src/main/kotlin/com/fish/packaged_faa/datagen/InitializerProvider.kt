package com.fish.packaged_faa.datagen

import com.fish.packaged_faa.PackagedFAA
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = PackagedFAA.MODID)
object InitializerProvider {
    @SubscribeEvent
    private fun regProvider(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val provider = event.lookupProvider
        val helperExisting = event.existingFileHelper

        generator.addProvider(event.includeClient(), LangZH(output))
        generator.addProvider(event.includeClient(), LangEN(output))
        generator.addProvider(event.includeServer(), Recipe(output, provider))
        generator.addProvider(event.includeServer(), LootTable.Provider(output, provider))

        val providerBlock = Tag.Blocks(output, provider, helperExisting)
        val providerItem = Tag.Items(output, provider, providerBlock.contentsGetter(), helperExisting)
        val providerFluid = Tag.Fluids(output, provider, helperExisting)
        generator.addProvider(event.includeServer(), providerBlock)
        generator.addProvider(event.includeServer(), providerItem)
        generator.addProvider(event.includeServer(), providerFluid)
    }
}