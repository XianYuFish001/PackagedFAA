package com.fish.packaged_faa.integration.jade.helper

import com.fish.packaged_faa.PackagedFAA
import com.mojang.datafixers.util.Pair
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import snownee.jade.api.Accessor
import snownee.jade.api.IServerDataProvider
import java.util.function.Consumer

class WrapperObjectedProvider<TAccessor : Accessor<*>>(
    val id: ResourceLocation,
    val providers: List<Pair<String, (CompoundTag, TAccessor) -> Unit>>
) : IServerDataProvider<TAccessor> {
    override fun appendServerData(serverData: CompoundTag, accessor: TAccessor) {
        this.providers.forEach(Consumer { provider ->
            val data = CompoundTag()
            provider.getSecond()(data, accessor)
            serverData.put(provider.getFirst(), data)
        })
    }

    override fun getUid() = this.id

    companion object {
        fun <TAccessor : Accessor<*>> create(
            uid: String,
            providers: List<Pair<String, (CompoundTag, TAccessor) -> Unit>>
        ) = WrapperObjectedProvider(PackagedFAA.getLocation(uid), providers)

        fun <TAccessor : Accessor<*>, TProvider>
                create(
            uid: String,
            clazzProvider: Class<TProvider>
        ): WrapperObjectedProvider<TAccessor>
        where TProvider : Enum<TProvider>, TProvider : IObjectedProvider<TAccessor> =
            this.create(uid, IObjectedProvider.getProviders(clazzProvider))
    }
}
