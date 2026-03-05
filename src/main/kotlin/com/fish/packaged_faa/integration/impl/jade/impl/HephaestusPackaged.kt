package com.fish.packaged_faa.integration.impl.jade.impl

import com.fish.fishlib.integration.jade.IObjectedAppenderBlock
import com.fish.fishlib.integration.jade.IObjectedProvider
import com.fish.fishlib.integration.jade.TooltipAppender
import com.fish.fishlib.util.keyBuilder.Patterns
import com.fish.fishlib.util.keyBuilder.collection
import com.fish.fishlib.util.keyBuilder.newArrayList
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.fish.packaged_faa.util.UtilKeyBuilder
import com.fish.packaged_faa.util.ifNotEmpty
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import snownee.jade.api.BlockAccessor

object HephaestusPackaged {
    enum class Provider(
        override val provider: (CompoundTag, BlockAccessor) -> Unit
    ) : IObjectedProvider<BlockAccessor> {
        Level(provider@{ data, accessor ->
            val state = accessor.blockState
            if (!state.hasProperty(BlockHephaestusPackaged.propertyLevelForge)) return@provider
            val level = state.getValue(BlockHephaestusPackaged.propertyLevelForge)
            data.putInt("level", level)
        }),
        Essences(provider@{ data, accessor ->
            val tile = accessor.blockEntity as? TileHephaestusPackaged ?: return@provider
            val storageEssences = tile.essences()
            EssenceType.entries.forEach { type ->
                val essence = storageEssences[type]
                if (essence == 0) return@forEach
                data.putInt(type.serializedName, essence)
            }
        }),
        Enhancers(provider@{ data, accessor ->
            val tile = accessor.blockEntity as? TileHephaestusPackaged ?: return@provider
            val enhancers = tile.enhancers()
            val dataList = ListTag()
            enhancers
                .map { it.value().displayItem.value().defaultInstance.displayName.string }
                .map { StringTag.valueOf(it) }
                .forEach { dataList.add(it) }
            data.put("list", dataList)
        })
    }

    enum class Tooltip(override val appender: TooltipAppender) : IObjectedAppenderBlock {
        Level(tooltip@{ _, tooltip, _, data ->
            tooltip.add(
                UtilKeyBuilder.of(Patterns.JadeInfo)
                    .addStr("hephaestus")
                    .addStr("level")
                    .args(data.getInt("level"))
                    .build()
            )
        }),
        Essences(tooltip@{ _, tooltip, _, data ->
            val values = HashMap<String, Int>(4)
            EssenceType.entries.forEach { type ->
                if (!data.contains(type.serializedName)) return@forEach
                values[type.serializedName] = data.getInt(type.serializedName)
            }
            if (values.isEmpty()) return@tooltip

            val builder = UtilKeyBuilder.of(Patterns.JadeInfo)
                .addStr("hephaestus")
                .addStr("essences")
                .newArrayList()
                .buildInto()
            values.forEach { (name, amount) -> builder.args(amount).buildInto(name) }
            builder.collection?.forEach(tooltip::add)
        }),
        Enhancers(tooltip@{ _, tooltip, _, data ->
            data.getList("list", Tag.TAG_STRING.toInt())
                .filterNotNull()
                .ifNotEmpty {
                    tooltip.add(
                        UtilKeyBuilder.of(Patterns.JadeInfo)
                            .addStr("hephaestus")
                            .addStr("enhancers")
                            .build()
                    )
                }.forEach {
                    tooltip.add(Component.literal("- ${it.asString}"))
                }
        });

        override val id = PackagedFAA.getLocation("hephaestus.${this.name.lowercase()}")
    }
}