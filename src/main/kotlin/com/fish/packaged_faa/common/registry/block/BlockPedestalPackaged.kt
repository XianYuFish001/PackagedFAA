package com.fish.packaged_faa.common.registry.block

import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.common.registry.block.tile.TilePedestalPackaged
import com.mojang.serialization.MapCodec
import com.stal111.forbidden_arcanus.common.block.entity.PedestalBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BlockPedestalPackaged(properties: Properties) : BaseEntityBlock(properties.dynamicShape()) {
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, tile ->
        if (tile !is TilePedestalPackaged) return@BlockEntityTicker
        if (level.isClientSide) PedestalBlockEntity.clientTick(level, pos, state, tile)
        else PedestalBlockEntity.serverTick(level, pos, state, tile)
    }

    override fun codec(): MapCodec<BlockPedestalPackaged> = MapCodec.unit(PFAABlocks.pedestalPackaged.get())

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext) = shape

    override fun newBlockEntity(p0: BlockPos, p1: BlockState) = TilePedestalPackaged(p0, p1)

    companion object {
        private val shape: VoxelShape = Shapes.or(
            box(1.0, 0.0, 1.0, 15.0, 4.0, 15.0),
            box(3.0, 4.0, 3.0, 13.0, 6.0, 13.0),
            box(4.0, 6.0, 4.0, 12.0, 11.0, 12.0),
            box(2.0, 11.0, 2.0, 14.0, 14.0, 14.0)
        )
    }
}