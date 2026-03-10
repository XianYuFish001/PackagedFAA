package com.fish.packaged_faa

import com.fish.fishlib.common.InitObject
import com.fish.fishlib.config.HelperConfig
import com.fish.fishlib.config.HelperConfig.Companion.bind
import com.fish.fishlib.config.section
import com.fish.fishlib.config.spec
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig

object PFAAConfig {
    private val HelperCommon: HelperConfig = HelperConfig(::SpecCommon)
    private val HelperServer: HelperConfig = HelperConfig(::SpecServer)

    // Common

    var LoggedHephaestus: Boolean by HelperCommon

    // Server

    var SoulExtractReturns: Boolean by HelperServer
    val FrequencyEssenceCollect: Int by HelperServer

    // Factors
    var FactorAureal: Int by HelperServer
    var FactorBlood: Int by HelperServer
    var FactorSouls: Int by HelperServer
    var FactorExperience: Int by HelperServer

    private val SpecCommon by spec(ModConfig.Type.COMMON) { spec ->
        spec.define("logged_hephaestus", false)
            .bind(HelperCommon, ::LoggedHephaestus)
    }

    private val SpecServer by spec(ModConfig.Type.SERVER) { spec ->
        spec.define("soul_extract_returns", false)
            .bind(HelperServer, ::SoulExtractReturns)
        spec.defineInRange("frequency_essence_collect", 10, 0, 160)
            .bind(HelperServer, ::FrequencyEssenceCollect)

        spec.section("factor") { section ->
            section.defineInRange("aureal", 10, 0, 128)
                .bind(HelperServer, ::FactorAureal)
            section.defineInRange("blood", 10, 0, 128)
                .bind(HelperServer, ::FactorBlood)
            section.defineInRange("souls", 10, 0, 128)
                .bind(HelperServer, ::FactorSouls)
            section.defineInRange("experience", 10, 0, 128)
                .bind(HelperServer, ::FactorExperience)
        }
    }

    @InitObject
    private fun init(containerMod: ModContainer) {
        HelperCommon.init(containerMod)
        HelperServer.init(containerMod)
    }
}