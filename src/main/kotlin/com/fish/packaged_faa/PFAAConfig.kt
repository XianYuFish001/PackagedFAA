package com.fish.packaged_faa

import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec

object PFAAConfig {
    internal val specServer: Triple<ModConfig.Type, ModConfigSpec, String>

    lateinit var soulExtractReturns: ModConfigSpec.BooleanValue

    init {
        this.specServer = this.spec("server", ModConfig.Type.SERVER) { spec ->
            this.soulExtractReturns = spec.define("soul_extract_returns", false)
        }
    }

    internal fun init(containerMod: ModContainer) {
        containerMod.registerConfig(
            specServer.first,
            specServer.second,
            "packaged_faa/${specServer.third}.toml"
        )
    }

    private inline fun spec(
        spec: String, type: ModConfig.Type, modifier: (ModConfigSpec.Builder) -> Unit
    ): Triple<ModConfig.Type, ModConfigSpec, String> {
        val builder = ModConfigSpec.Builder()
        modifier(builder)
        return Triple(type, builder.build(), spec)
    }

    private inline fun ModConfigSpec.Builder.section(
        section: String, modifier: (ModConfigSpec.Builder) -> Unit
    ): ModConfigSpec.Builder {
        this.push(section)
        modifier(this)
        this.pop()
        return this
    }
}