package com.fish.packaged_faa

import com.fish.packaged_faa.common.init.*
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(PackagedFAA.MODID)
class PackagedFAA(eventBus: IEventBus, containerMod: ModContainer) {
    init {

        PFAAItems.register.register(eventBus)
        PFAABlocks.register.register(eventBus)
        PFAATiles.register.register(eventBus)
        PFAADataComponents.register.register(eventBus)
        PFAAFluids.registerFluid.register(eventBus)
        PFAAFluids.registerType.register(eventBus)

        RegistriesOther.init()
        PFAAConfig.init(containerMod)
    }

    companion object {
        const val MODID = "packaged_faa"

        fun getLocation(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)
    }
}