package com.fish.packaged_faa.datagen

import com.fish.fishlib.util.keyBuilder.ContainerDataGen
import com.fish.fishlib.util.keyBuilder.Patterns
import com.fish.fishlib.util.keyBuilder.toKeyPattern
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.util.UtilKeyBuilder
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class LangEN(output: PackOutput) : LanguageProvider(output, PackagedFAA.MODID, "en_us") {
    override fun addTranslations() = ContainerDataGen.with("en_us", this::add) {
        this.addBlock(PFAABlocks.hephaestusPackaged, "Packaged Hephaestus Forge")
        this.addBlock(PFAABlocks.pedestalPackaged, "Packaged Pedestal")

        UtilKeyBuilder.dataGen("fluid.type.%s%s".toKeyPattern())
            .branch("type_aureal", "Aureal")
            .branch("type_souls", "Souls")
            .branch("type_blood", "Blood")
            .branch("type_experience", "Experience")
        UtilKeyBuilder.dataGen("fluid.%s%s".toKeyPattern())
            .branch("fluid_aureal", "Aureal")
            .branch("fluid_souls", "Souls")
            .branch("fluid_blood", "Blood")
            .branch("fluid_experience", "Experience")

        UtilKeyBuilder.dataGen(Patterns.ScreenTooltip)
            .addStr("type_recipe")
            .addStr("hephaestus")
            .branch("short", "Hephaestus Smithing")
            .buildInto("Hephaestus Smithing")

        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("hephaestus")
            .branch("level", "Forge Level: %s")
            .branch("enhancers", "Enhancers:")
        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("hephaestus")
            .addStr("essences")
            .branch("aureal", "- Aureal %s")
            .branch("blood", "- Blood  %s")
            .branch("souls", "- Souls %s")
            .branch("experience", "- Experience %s")
            .buildInto("Essences:")
        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("log")
            .branch("idle", "Idle")
            .section("hephaestus_packaged") { it
                .section("0") { it
                    .branch("pre", "[Precheck] Pedestals are not enough (%s/%s)")
                    .branch("exec", "[Executing] Pedestals are not enough")
                }
                .section("1") { it
                    .branch("pre.uuid", "[Precheck] Stored UUID is null, replace forge")
                    .buildInto("[Executing] Ritual failed (First)")
                }
                .section("2") { it
                    .branch("pre", "[Precheck] Block{%s} with no FluidHandler")
                    .section("exec") { it
                        .branch("empty", "[Executing] Tank{%s} with empty fluid")
                        .branch("wrong", "[Executing] Tank{%s} with wrong fluid")
                    }
                }
                .branch("3", "[Executing] Ritual failed (Second)")
                .section("internal") { it
                    .branch("essence", "[Executing] Insufficient essences")
                    .branch("tier", "[Executing] Tier is not within bounds] ")
                    .branch("enhancer", "[Executing] Missing enhancer")
                }
            }

        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("soul_extract_returns")
            .branch("tooltip", "Controls whether jar will leave Soulless Sand behind when absorbing Soul Sand/Soil around them.")
            .buildInto("Soul Extract Returns")
        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("logged_hephaestus")
            .branch("tooltip", "Controls whether Forge writes the reason for packaged receiving failure to DebugLog \nTips: May pollute the log, enable with caution")
            .buildInto("Logged Hephaestus Running")
        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("frequency_essence_collect")
            .branch("tooltip", "Control the tick interval between two essence collection\n0 means no collection")
            .buildInto("Essence Collect Frequency")
        UtilKeyBuilder.dataGen(Patterns.Config)
            .branch("aureal", "Aureal")
            .branch("souls", "Souls")
            .branch("blood", "Blood")
            .branch("experience", "Experience")
            .addStr("factor")
            .branch("tooltip", "Controls the essence value each jar collection")
            .buildInto("Essence Collection Factor")
    }
}