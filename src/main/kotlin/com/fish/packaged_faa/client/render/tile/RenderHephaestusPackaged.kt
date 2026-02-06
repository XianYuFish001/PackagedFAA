package com.fish.packaged_faa.client.render.tile

import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.stal111.forbidden_arcanus.client.model.MagicCircleModel
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.AABB
import javax.annotation.Nonnull

class RenderHephaestusPackaged(private val context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<TileHephaestusPackaged> {
    private val modelCircle: MagicCircleModel = MagicCircleModel(this.context)

    override fun render(
        @Nonnull tile: TileHephaestusPackaged,
        partialTicks: Float,
        @Nonnull poseStack: PoseStack,
        @Nonnull bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val magicCircle = tile.controllerCircle.magicCircle

        magicCircle?.render(
            poseStack,
            partialTicks,
            bufferSource,
            packedLight,
            this.modelCircle,
            tile.durationRitual
        )

        tile.indicatorRitual?.render(
            poseStack,
            partialTicks,
            bufferSource,
            packedLight,
            this.modelCircle.validRitualIndicator()
        )

        val stack = tile.inputMain

        if (stack.isEmpty) return

        poseStack.pushPose()

        poseStack.translate(0.5, 1.3, 0.5)
        poseStack.mulPose(Axis.YP.rotation((tile.counterDisplay + partialTicks) / 20))

        poseStack.scale(0.5f, 0.5f, 0.5f)

        Minecraft.getInstance().itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            packedLight,
            packedOverlay,
            poseStack,
            bufferSource,
            tile.getLevel(),
            0
        )

        poseStack.popPose()
    }

    override fun shouldRenderOffScreen(@Nonnull tile: TileHephaestusPackaged) =
        this.useExpandedRenderBoundingBox(tile)

    override fun getRenderBoundingBox(tile: TileHephaestusPackaged): AABB {
        var boundingBox = super.getRenderBoundingBox(tile).expandTowards(0.0, 1.0, 0.0)

        if (this.useExpandedRenderBoundingBox(tile))
            boundingBox = boundingBox.inflate(2.5, 0.0, 2.5)
        return boundingBox
    }

    fun useExpandedRenderBoundingBox(tile: TileHephaestusPackaged) =
        tile.managerRitual.isRitualActive || tile.indicatorRitual != null
}