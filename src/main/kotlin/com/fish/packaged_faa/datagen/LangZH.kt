package com.fish.packaged_faa.datagen

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.util.UtilKeyBuilder
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class LangZH(output: PackOutput) : LanguageProvider(output, PackagedFAA.MODID, "zh_cn") {
    private fun buildInner() {
        this.addBlock(PFAABlocks.hephaestusPackaged, "封包赫菲斯托斯锻炉")
        this.addBlock(PFAABlocks.pedestalPackaged, "封包基座")

        UtilKeyBuilder.ofDataGen("fluid.type.%s%s")
            .branch("type_aureal", "耀金")
            .branch("type_souls", "灵魂")
            .branch("type_blood", "血")
            .branch("type_experience", "经验")
        UtilKeyBuilder.ofDataGen("fluid.%s%s")
            .branch("fluid_aureal", "耀金")
            .branch("fluid_souls", "灵魂")
            .branch("fluid_blood", "血")
            .branch("fluid_experience", "经验")

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.screenTooltip)
            .addStr("type_recipe")
            .addStr("hephaestus")
            .branch("short", "赫菲斯托斯锻造")
            .buildInto("赫菲斯托斯锻造")

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.jadeInfo)
            .addStr("hephaestus")
            .branch("level", "锻炉等级: %s")
//            .branch("enhancers", "")
        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.jadeInfo)
            .addStr("hephaestus")
            .addStr("essences")
            .branch("aureal", "- 耀金 %s")
            .branch("blood", "- 血   %s")
            .branch("souls", "- 灵魂 %s")
            .branch("experience", "- 经验 %s")
            .buildInto("精华:")

        UtilKeyBuilder.ofDataGen(UtilKeyBuilder.config)
            .addStr("soul_extract_returns")
            .branch("tooltip", "控制瓶罐在通过吸取周围的灵魂沙/土获取灵魂时是否会留下失魂沙")
            .buildInto("灵魂提取返还")
    }

    override fun addTranslations() {
        UtilKeyBuilder.BuilderDataGen.bindTranslator("zh_cn", this::add)
        this.buildInner()
        UtilKeyBuilder.BuilderDataGen.destroy("zh_ch")
    }
}