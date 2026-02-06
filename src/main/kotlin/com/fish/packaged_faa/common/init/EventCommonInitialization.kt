package com.fish.packaged_faa.common.init

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.packagedAuto.recipe.TypeHephaestus
import com.fish.packaged_faa.mixin.extension.ExtensionClibanoHandler.Companion.helperTransfer
import com.fish.packaged_faa.mixin.extension.ExtensionJarEssence
import com.fish.packaged_faa.mixin.extension.ExtensionTankCrystal.Companion.tankAureal
import com.stal111.forbidden_arcanus.ForbiddenArcanus
import com.stal111.forbidden_arcanus.core.init.ModBlockEntities
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import thelm.packagedauto.api.PackagedAutoApi

@EventBusSubscriber(modid = PackagedFAA.MODID)
object EventCommonInitialization {
    @SubscribeEvent
    private fun commonSetup(event: FMLCommonSetupEvent) {
        PackagedAutoApi.instance().registerRecipeType(TypeHephaestus.instance)
    }

    @SubscribeEvent
    private fun regCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        if (!ForbiddenArcanus.location("main").equals(event.tabKey.location())) return
        event.accept(PFAAItems.hephaestusPackaged)
        event.accept(PFAAItems.pedestalPackaged)
    }

    @SubscribeEvent
    private fun regCapability(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.ARCANE_CRYSTAL_OBELISK.get()
        ) { tile, _ -> tile.tankAureal }
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.ESSENCE_UTREM_JAR.get()
        ) { tile, _ -> ExtensionJarEssence.WrapperHandlerFluid(tile) }
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            PFAATiles.transmuterUtremJar.get()
        ) { tile, _ -> tile }
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.CLIBANO.get()
        ) { tile, _ -> tile.helperTransfer?.handlerItem() }
    }
}