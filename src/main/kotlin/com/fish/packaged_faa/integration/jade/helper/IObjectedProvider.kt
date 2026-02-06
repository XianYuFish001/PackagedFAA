package com.fish.packaged_faa.integration.jade.helper

import com.mojang.datafixers.util.Pair
import net.minecraft.nbt.CompoundTag
import snownee.jade.api.Accessor

interface IObjectedProvider<TAccessor : Accessor<*>> {
    val provider: (CompoundTag, TAccessor) -> Unit

    companion object {
        fun <TEntry, TAccessor : Accessor<*>> getProviders(
            clazzProvider: Class<TEntry>
        ): List<Pair<String, (CompoundTag, TAccessor) -> Unit>>
        where TEntry : Enum<TEntry>, TEntry : IObjectedProvider<TAccessor> {
            return clazzProvider.getEnumConstants()
                .map { entry ->
                    Pair(
                        entry.name.lowercase(),
                        entry.provider
                    )
                }
        }
    }
}