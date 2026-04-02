@file:Suppress("FunctionName")

package com.fish.packaged_faa.mixin.extension

import com.fish.packaged_faa.PFAAConfig
import com.fish.packaged_faa.common.registry.fluid.FluidEssence
import com.fish.packaged_faa.common.registry.fluid.FluidEssence.Companion.toStack
import com.fish.packaged_faa.util.UtilAttraction
import com.stal111.forbidden_arcanus.common.block.entity.EssenceUtremJarBlockEntity
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType
import com.stal111.forbidden_arcanus.common.block.properties.ModBlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.BlockPositionSource
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEventListener
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import java.lang.ref.WeakReference
import java.util.function.Consumer

interface ExtensionJarEssence {
    fun `pfaa$tickServer`(level: ServerLevel, pos: BlockPos, state: BlockState)

    fun `pfaa$tickClient`(level: Level, pos: BlockPos, state: BlockState)

    fun `pfaa$tick`(level: Level, pos: BlockPos, state: BlockState) = when (level) {
        is ServerLevel -> this.`pfaa$tickServer`(level, pos, state)
        else -> this.`pfaa$tickClient`(level, pos, state)
    }

    companion object {
        var EssenceUtremJarBlockEntity.typeEssence: EssenceType
            get() = this.blockState.getValue(ModBlockStateProperties.ESSENCE_TYPE)
            set(value) {
                this.level?.setBlock(
                    this.blockPos,
                    this.blockState.setValue(
                        ModBlockStateProperties.ESSENCE_TYPE, value
                    ),
                    Block.UPDATE_CLIENTS
                )
            }

        fun EssenceUtremJarBlockEntity.addEssenceUpdated(type: EssenceType? = null, amount: Int): Boolean {
            if (type != null && this.typeEssence != type) return false
            this.addEssence(amount)
            this.level?.sendBlockUpdated(
                this.blockPos,
                this.blockState,
                this.blockState,
                Block.UPDATE_CLIENTS
            )
            return true
        }

        fun EssenceUtremJarBlockEntity.castTo(type: EssenceType): Boolean {
            if (this.typeEssence != type) {
                if (this.amount > 0) return false
                this.typeEssence = type
            }
            return true
        }
    }

    class ListenerJar(private val tile: EssenceUtremJarBlockEntity) : GameEventListener {
        private val source = BlockPositionSource(this.tile.blockPos)
        private val consumed = hashSetOf<Vec3>()

        override fun getListenerSource() = this.source

        override fun getListenerRadius() = 4

        override fun handleGameEvent(
            level: ServerLevel,
            holderEvent: Holder<GameEvent>,
            context: GameEvent.Context,
            pos: Vec3
        ): Boolean {
            if (PFAAConfig.FactorBlood == 0) return false

            if (!holderEvent.`is`(GameEvent.ENTITY_DIE.key!!)) return false
            if (!this.entityValid(context.sourceEntity)) return false
            if (this.consumed.contains(pos)) return false

            val health = ((context.sourceEntity as? LivingEntity)?.maxHealth ?: 5F) *
                    PFAAConfig.FactorBlood
            if (!this.tile.addEssenceUpdated(
                    EssenceType.BLOOD, health.toInt()
            )) return false

            this.consumed += pos
            return true
        }

        override fun getDeliveryMode() = GameEventListener.DeliveryMode.BY_DISTANCE

        private fun entityValid(entity: Entity?) = entity == null || entity is LivingEntity
    }

    class CollectorExperience(
        private val action: Consumer<ExperienceOrb>
    ) {
        private val entities = ArrayList<WeakReference<ExperienceOrb>>()

        fun tick(level: Level, pos: BlockPos): Boolean {
            val frequency = PFAAConfig.FrequencyEssenceCollect
            if (frequency == 0) return false
            if (level.gameTime % frequency == 0L)
                this.updateEntities(level, pos)

            var collected = false
            val iteratorEntities = this.entities.iterator()
            while (iteratorEntities.hasNext()) {
                val entity = iteratorEntities.next().get()
                if (entity?.isRemoved ?: true) {
                    iteratorEntities.remove()
                    continue
                }

                if (!UtilAttraction.moveToPos(
                        entity,
                        pos,
                        SPEED_VERTICAL,
                        SPEED_HORIZONTAL,
                        BOX
                )) continue
                if (level.isClientSide) continue
                this.action.accept(entity)
                collected = true
            }
            return collected
        }

        private fun updateEntities(level: Level, pos: BlockPos) = this.entities
            .also(ArrayList<*>::clear)
            .apply {
                level.getEntitiesOfClass(
                    ExperienceOrb::class.java,
                    AABB(pos).inflate(4.0)
                )
                    .map(::WeakReference)
                    .forEach(this::add)
            }

        companion object {
            const val SPEED_VERTICAL = 0.025
            const val SPEED_HORIZONTAL = SPEED_VERTICAL * 4
            const val BOX = 1.0 * 1.0
        }
    }

    class WrapperHandlerFluid(private val tile: EssenceUtremJarBlockEntity) : IFluidHandler {
        override fun getTanks() = 1

        override fun getFluidInTank(slot: Int): FluidStack {
            if (slot != 0) return FluidStack.EMPTY
            return this.tile.typeEssence.toStack(this.tile.amount)
        }

        override fun getTankCapacity(slot: Int): Int {
            if (slot != 0) return 0
            return this.tile.limit
        }

        override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
            if (slot != 0) return false
            return FluidEssence.fromStack(stack, false)?.first == this.tile.typeEssence
        }

        override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
            if (!this.isFluidValid(0, stack)) {
                if (this.tile.amount > 0) return 0
                val type = (stack.fluid as? FluidEssence)?.type
                    ?: FluidEssence.convertFrom(stack) ?: return 0
                this.tile.typeEssence = type
            }

            val amount = stack.amount.coerceAtMost(this.tile.limit - this.tile.amount)
            if (action == IFluidHandler.FluidAction.EXECUTE)
                this.tile.addEssenceUpdated(amount = amount)
            return amount
        }

        override fun drain(stack: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
            if (!this.isFluidValid(0, stack)) return FluidStack.EMPTY

            val amount = stack.amount.coerceAtMost(this.tile.amount)
            if (action == IFluidHandler.FluidAction.EXECUTE) this.tile.addEssenceUpdated(amount = -amount)
            return stack.copyWithAmount(amount)
        }

        override fun drain(amount: Int, action: IFluidHandler.FluidAction) =
            this.drain(this.tile.typeEssence.toStack(amount), action)
    }
}