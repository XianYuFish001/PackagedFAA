package com.fish.packaged_faa.common.init

import com.fish.fishlib.common.InitObject
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object PFAADataComponents {
    @InitObject
    val register: DeferredRegister.DataComponents =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PackagedFAA.MODID)

    val levelForge = register(
        "level_forge",
        BlockHephaestusPackaged.DataLevel.codec,
        BlockHephaestusPackaged.DataLevel.streamCodec
    )

    private fun <T> register(
        name: String, codec: Codec<T>?, streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>?
    ): DeferredHolder<DataComponentType<*>, DataComponentType<T>> = register.registerComponentType<T>(name) {
        it.persistent(codec).networkSynchronized(streamCodec)
    }
}