package com.fish.packaged_faa.common.registry.block

import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.common.init.PFAADataComponents
import com.fish.packaged_faa.common.init.PFAAItems
import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeLevel
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs
import thelm.packagedauto.block.BaseBlock

class BlockHephaestusPackaged(properties: Properties) : BaseBlock(properties) {
    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(propertyLevelForge, 1)
        )
    }

    override fun codec(): MapCodec<BlockHephaestusPackaged> = MapCodec.unit(PFAABlocks.hephaestusPackaged::get)

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = TileHephaestusPackaged(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, tile ->
        if (tile !is TileHephaestusPackaged) return@BlockEntityTicker
        if (level.isClientSide) tile.tickClient()
        else tile.tickServer()
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(propertyLevelForge)
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player
    ): ItemStack {
        val item = super.getCloneItemStack(state, target, level, pos, player)
        item.set(PFAADataComponents.levelForge, DataLevel(getLevelForge(state)))
        return item
    }

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ) = InteractionResult.PASS

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (stack.isEmpty)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        val tile = level.getBlockEntity(pos)
        if (tile !is TileHephaestusPackaged)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        if (tile.putEnhancer(stack)) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS
            stack.count -= 1
            return ItemInteractionResult.CONSUME
        } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide)
            return super.onRemove(state, level, pos, newState, isMoving)
        if (newState.block is BlockHephaestusPackaged || isMoving)
            return super.onRemove(state, level, pos, newState, isMoving)

        val result = mutableSetOf<ItemStack>()

        val itemSelf = PFAAItems.hephaestusPackaged.toStack()
        itemSelf.set(PFAADataComponents.levelForge,
            DataLevel(HephaestusForgeLevel.getFromIndex(state.getValue(propertyLevelForge))))
        result.add(itemSelf)

        (level.getBlockEntity(pos) as? TileHephaestusPackaged)?.drop(result)

        result.forEach { stack ->
            Containers.dropItemStack(
                level,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                stack
            )
        }

        super.onRemove(state, level, pos, newState, false)
    }

    override fun triggerEvent(state: BlockState, level: Level, pos: BlockPos, id: Int, param: Int): Boolean {
        super.triggerEvent(state, level, pos, id, param)
        return level.getBlockEntity(pos)?.triggerEvent(id, param) ?: false
    }

    override fun getShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ) = shape

    data class DataLevel(val level: HephaestusForgeLevel) {
        companion object {
            val codec: Codec<DataLevel> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.xmap({ HephaestusForgeLevel.getFromIndex(it) }, { it.asInt })
                        .fieldOf("level").forGetter { it.level }
                ).apply(instance, ::DataLevel)
            }

            val streamCodec: StreamCodec<RegistryFriendlyByteBuf, DataLevel> = StreamCodec.composite(
                NeoForgeStreamCodecs.enumCodec(HephaestusForgeLevel::class.java),
                { it.level },
                ::DataLevel
            )
        }
    }

    companion object {
        val propertyLevelForge: IntegerProperty = IntegerProperty.create("level_forge", 1, 5)

        private val shape: VoxelShape = Shapes.join(
            Shapes.or(
                box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0),
                box(2.0, 3.0, 2.0, 14.0, 4.0, 14.0),
                box(4.0, 4.0, 4.0, 12.0, 8.0, 12.0),
                box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0)
            ),
            Shapes.or(
                box(0.0, 15.0, 3.0, 16.0, 16.0, 13.0),
                box(3.0, 15.0, 0.0, 13.0, 16.0, 16.0)
            ),
            BooleanOp.ONLY_FIRST
        )

        fun getLevelForge(state: BlockState): HephaestusForgeLevel {
            if (state.block !is BlockHephaestusPackaged) return HephaestusForgeLevel.ONE
            return HephaestusForgeLevel.getFromIndex(state.getValue(propertyLevelForge))
        }
    }
}