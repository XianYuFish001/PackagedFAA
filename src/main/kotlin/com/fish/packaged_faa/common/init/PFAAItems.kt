package com.fish.packaged_faa.common.init

import com.fish.fishlib.common.InitObject
import com.fish.fishlib.util.extension.invoke
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.item.ItemHephaestusPackaged
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object PFAAItems {
    @InitObject
    val register: DeferredRegister.Items = DeferredRegister.createItems(PackagedFAA.MODID)

    val hephaestusPackaged: DeferredItem<Item> = register.register("hephaestus_packaged", ::ItemHephaestusPackaged)
    val pedestalPackaged: DeferredItem<Item> = register.register("pedestal_packaged") { ->
        BlockItem(PFAABlocks.pedestalPackaged(), Item.Properties())
    }
}