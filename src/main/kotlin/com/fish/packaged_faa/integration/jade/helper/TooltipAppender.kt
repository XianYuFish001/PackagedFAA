package com.fish.packaged_faa.integration.jade.helper

import net.minecraft.nbt.CompoundTag
import snownee.jade.api.BlockAccessor
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

fun interface TooltipAppender {
    fun add(accessor: BlockAccessor, tooltip: ITooltip, config: IPluginConfig, data: CompoundTag)

    fun add(name: String, accessor: BlockAccessor, tooltip: ITooltip, config: IPluginConfig) {
        this.add(accessor, tooltip, config, accessor.serverData.getCompound(name.lowercase()) ?: return)
    }
}
