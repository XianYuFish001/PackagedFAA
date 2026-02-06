package com.fish.packaged_faa

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod(PackagedFAA.MODID, dist = [Dist.CLIENT])
class PackagedFAAClient(containerMod: ModContainer) {
    init {
        containerMod.registerExtensionPoint(IConfigScreenFactory::class.java,
            IConfigScreenFactory(::ConfigurationScreen))
    }
}