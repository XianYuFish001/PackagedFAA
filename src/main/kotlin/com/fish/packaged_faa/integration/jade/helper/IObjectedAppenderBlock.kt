package com.fish.packaged_faa.integration.jade.helper

import net.minecraft.resources.ResourceLocation
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

interface IObjectedAppenderBlock : IBlockComponentProvider {
    val appender: TooltipAppender

    val name: String

    val id: ResourceLocation

    override fun getUid() = this.id

    override fun appendTooltip(
        tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig
    ) = this.appender.add(this.name.lowercase(), accessor, tooltip, config)
}