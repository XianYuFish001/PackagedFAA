package com.fish.packaged_faa.integration.impl.jade

import com.fish.fishlib.integration.jade.TooltipAppender
import com.fish.fishlib.util.keyBuilder.Patterns
import com.fish.packaged_faa.util.UtilKeyBuilder
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import snownee.jade.api.BlockAccessor

class ContainerLog(private val keyMachine: String) {
    private var style = 0
    private var key = ""
    private var args = emptyList<String>()

    fun idle() {
        this.style = 0
        this.key = ""
    }

    @JvmOverloads
    fun warn(vararg key: String, args: List<String> = emptyList()) {
        if (this.style == 2) return
        this.style = 1
        this.key = key.joinToString(".")
        this.args = args
    }

    @JvmOverloads
    fun error(vararg key: String, args: List<String> = emptyList()) {
        this.style = 2
        this.key = key.joinToString(".")
        this.args = args
    }

    companion object {
        private val styleIdle = arrayOf(ChatFormatting.GREEN)
        private val styleWarn = arrayOf(ChatFormatting.GOLD)
        private val styleError = arrayOf(ChatFormatting.RED, ChatFormatting.BOLD)

        private fun textIdle(): Component = UtilKeyBuilder.of(Patterns.JadeInfo)
            .addStr("log")
            .addStr("idle")
            .build()
            .withStyle(*styleIdle)

        fun provider(
            locator: (BlockEntity) -> ContainerLog?
        ) = provider@{ data: CompoundTag, accessor: BlockAccessor ->
            val container = locator(accessor.blockEntity) ?: return@provider
            data.putString("key_machine", container.keyMachine)
            data.putInt("state_style", container.style)
            data.putString("state_key", container.key)

            if (container.args.isEmpty()) return@provider
            val args = ListTag(container.args.size)
            container.args.forEach {
                args += StringTag.valueOf(it)
            }
            data.put("args", args)
        }

        val tooltip = TooltipAppender { _, tooltip, _, data ->
            val keyMachine = data.getString("key_machine")
            val stateStyle = data.getInt("state_style")
            val stateKey = data.getString("state_key")
            val args = data.getList("args", 8)

            val text = UtilKeyBuilder.of(Patterns.JadeInfo)
                .addStr("log")
                .addStr(keyMachine)
                .addStr(stateKey)
                .args(*args.map { it.asString }.toTypedArray())
                .build()
            when (stateStyle) {
                0 -> textIdle()
                1 -> text.withStyle(*styleWarn)
                2 -> text.withStyle(*styleError)
                else -> throw IllegalArgumentException("Unexpected state style: $stateStyle")
            }.let(tooltip::add)
        }
    }
}