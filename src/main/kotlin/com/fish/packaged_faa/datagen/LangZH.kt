package com.fish.packaged_faa.datagen

import com.fish.fishlib.util.keyBuilder.ContainerDataGen
import com.fish.fishlib.util.keyBuilder.Patterns
import com.fish.fishlib.util.keyBuilder.toKeyPattern
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.util.UtilKeyBuilder
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class LangZH(output: PackOutput) : LanguageProvider(output, PackagedFAA.MODID, "zh_cn") {
    private fun doBuild() {
        this.addBlock(PFAABlocks.hephaestusPackaged, "封包赫菲斯托斯锻炉")
        this.addBlock(PFAABlocks.pedestalPackaged, "封包基座")

        UtilKeyBuilder.dataGen("fluid.type.%s%s".toKeyPattern())
            .branch("type_aureal", "耀金")
            .branch("type_souls", "灵魂")
            .branch("type_blood", "血")
            .branch("type_experience", "经验")
        UtilKeyBuilder.dataGen("fluid.%s%s".toKeyPattern())
            .branch("fluid_aureal", "耀金")
            .branch("fluid_souls", "灵魂")
            .branch("fluid_blood", "血")
            .branch("fluid_experience", "经验")

        UtilKeyBuilder.dataGen(Patterns.ScreenTooltip)
            .addStr("type_recipe")
            .addStr("hephaestus")
            .branch("short", "赫菲斯托斯锻造")
            .buildInto("赫菲斯托斯锻造")

        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("hephaestus")
            .branch("level", "锻炉等级: %s")
//            .branch("enhancers", "")
        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("hephaestus")
            .addStr("essences")
            .branch("aureal", "- 耀金 %s")
            .branch("blood", "- 血   %s")
            .branch("souls", "- 灵魂 %s")
            .branch("experience", "- 经验 %s")
            .buildInto("精华:")
        UtilKeyBuilder.dataGen(Patterns.JadeInfo)
            .addStr("log")
            .branch("idle", "锻炉运转正常")
            .section("hephaestus_packaged") { it
                .section("0") { it
                    .branch("pre", "[预检测] 基座数量不足 (%s/%s)")
                    .branch("exec", "[执行] 基座数量不足")
                }
                .section("1") { it
                    .branch("pre.uuid", "[预检测] 锻炉储存UUID为空, 请重新放置锻炉")
                    .buildInto("[执行] 第一次唤起仪式失败")
                }
                .section("2") { it
                    .branch("pre", "[预检测] 未找到处在{%s}方块的FluidHandler")
                    .section("exec") { it
                        .branch("empty", "[执行] 槽位{%s}的流体为空")
                        .branch("wrong", "[执行] 槽位{%s}的流体不匹配")
                    }
                }
                .branch("3", "[执行] 第二次唤起仪式失败")
                .section("internal") { it
                    .branch("essence", "[执行] 精华不足")
                    .branch("tier", "[执行] 锻炉等级不匹配")
                    .branch("enhancer", "[执行] Enhancer缺失")
                }
            }

        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("soul_extract_returns")
            .branch("tooltip", "控制瓶罐在通过吸取周围的灵魂沙/土获取灵魂时是否会留下失魂沙")
            .buildInto("灵魂提取返还")
        UtilKeyBuilder.dataGen(Patterns.Config)
            .addStr("logged_hephaestus")
            .branch("tooltip", "控制锻炉是否将封包发配失败原因写入DebugLog \n注意: 污染日志, 谨慎开启")
            .buildInto("锻炉发配日志")
    }

    override fun addTranslations() {
        ContainerDataGen.bind("zh_cn", this::add)
        this.doBuild()
        ContainerDataGen.destroy("zh_ch")
    }
}