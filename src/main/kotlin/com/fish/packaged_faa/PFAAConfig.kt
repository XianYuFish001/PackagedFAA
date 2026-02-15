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
        val register = { spec: Triple<ModConfig.Type, ModConfigSpec, String> ->
            containerMod.registerConfig(
                spec.first,
                spec.second,
                "packaged_faa/${spec.third}.toml"
            )
        }

        register(this.specServer)
    }

    private fun spec(
        spec: String, type: ModConfig.Type, modifier: (ModConfigSpec.Builder) -> Unit
    ): Triple<ModConfig.Type, ModConfigSpec, String> {
        val builder = ModConfigSpec.Builder()
        modifier(builder)
        return Triple(type, builder.build(), spec)
    }

    private fun ModConfigSpec.Builder.section(
        section: String, modifier: (ModConfigSpec.Builder) -> Unit
    ): ModConfigSpec.Builder {
        this.push(section)
        modifier(this)
        this.pop()
        return this
    }
}