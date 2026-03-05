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
    private fun doBuild() {
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

        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("soul_extract_returns")
            .branch("tooltip", "Controls whether jar will leave Soulless Sand behind when absorbing Soul Sand/Soil around them.")
            .buildInto("Soul Extract Returns")
    }

    override fun addTranslations() {
        ContainerDataGen.bind("en_us", this::add)
        this.doBuild()
        ContainerDataGen.destroy("en_us")
    }
}