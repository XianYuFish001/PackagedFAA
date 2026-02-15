package com.fish.packaged_faa.client.init

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.client.render.tile.RenderHephaestusPackaged
import com.fish.packaged_faa.common.init.PFAADataComponents
import com.fish.packaged_faa.common.init.PFAAFluids
import com.fish.packaged_faa.common.init.PFAAItems
import com.fish.packaged_faa.common.init.PFAATiles
import com.fish.packaged_faa.common.registry.fluid.FluidEssence
import com.stal111.forbidden_arcanus.client.renderer.block.PedestalRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent

@EventBusSubscriber(modid = PackagedFAA.MODID, value = [Dist.CLIENT])
object EventClientInitialization {
    @SubscribeEvent
    private fun regExtensions(event: RegisterClientExtensionsEvent) {
        event.registerFluidType(
            FluidEssence.ExtensionClientFluid("aureal"),
            PFAAFluids.typeAureal.get())
        event.registerFluidType(
            FluidEssence.ExtensionClientFluid("blood"),
            PFAAFluids.typeBlood.get())
        event.registerFluidType(
            FluidEssence.ExtensionClientFluid("souls"),
            PFAAFluids.typeSouls.get())
        event.registerFluidType(
            FluidEssence.ExtensionClientFluid("experience"),
            PFAAFluids.typeExperience.get())
    }

    @SubscribeEvent
    private fun regRender(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(
            PFAATiles.pedestalPackaged.get(),
            ::PedestalRenderer
        )
        event.registerBlockEntityRenderer(
            PFAATiles.hephaestusPackaged.get(),
            ::RenderHephaestusPackaged
        )
    }

    @SubscribeEvent
    private fun regModelAdditional(event: ModelEvent.RegisterAdditional) {
        ItemProperties.register(
            PFAAItems.hephaestusPackaged.get(),
            PackagedFAA.getLocation("level")) { stack, _, _, _ ->
            stack.get(PFAADataComponents.levelForge)
                ?.level
                ?.asInt
                ?.toFloat()
                ?: 1F
        }
    }
}