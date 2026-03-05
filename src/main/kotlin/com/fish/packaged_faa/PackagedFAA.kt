package com.fish.packaged_faa

import com.fish.fishlib.common.InitializerObject
import com.fish.packaged_faa.integration.helper.ManagerIntegration
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(PackagedFAA.MODID)
class PackagedFAA(eventBus: IEventBus, containerMod: ModContainer) {
    init {
        InitializerObject(eventBus, containerMod)
        // 暂时还没有
        // InitializerPacket(containerMod)

        ManagerIntegration.init()
    }

    companion object {
        const val MODID = "packaged_faa"

        fun getLocation(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, path)

        fun String.location() = this@Companion.getLocation(this)
    }
}