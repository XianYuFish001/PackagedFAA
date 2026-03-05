package com.fish.packaged_faa.integration.impl.bean

import appeng.api.crafting.IPatternDetails
import appeng.api.features.IPlayerRegistry
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.stacks.AEFluidKey
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKey
import appeng.core.network.clientbound.CraftingJobStatusPacket
import appeng.me.cluster.implementations.CraftingCPUCluster
import appeng.me.helpers.IGridConnectedBlockEntity
import com.fish.fishlib.integration.BeanIntegration
import com.fish.packaged_faa.integration.impl.point.IntegrationAE
import com.fish.packaged_faa.mixin.core.accessor.AccessorCraftingLogic
import com.fish.packaged_faa.mixin.core.accessor.AccessorCraftingLogicAdv
import com.fish.packaged_faa.mixin.helper.HelperCraftingJob
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Tuple
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.network.PacketDistributor
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU

@BeanIntegration("ae2")
object ImplAE : IntegrationAE {
    override fun cancelTask(
        key: Any,
        level: ServerLevel,
        posDeviceNetworked: BlockPos,
        tip: Boolean
    ) = this.taskProcess(key, level, posDeviceNetworked, tip) { key, canceler, helper ->
        val progress = helper.tasks.entries
            .find { key.matches(it.key) }
            ?.value
            ?.value
            ?: return@taskProcess
        if (progress > 1) return@taskProcess

        canceler()
    }

    override fun cancelTaskInWaiting(
        key: Any,
        level: ServerLevel,
        posDeviceNetworked: BlockPos,
        tip: Boolean
    ) = this.taskProcess(key, level, posDeviceNetworked, tip) { key, canceler, helper ->
        if (helper.waitingFor.list.lastOrNull()?.key != key) return@taskProcess
        canceler()
    }

    override fun findDevice(pos: BlockPos, level: ServerLevel) = Direction.entries
        .asSequence()
        .map(pos::relative)
        .map(level::getBlockEntity)
        .filterIsInstance<IGridConnectedBlockEntity>()
        .filterIsInstance<BlockEntity>()
        .mapNotNull(BlockEntity::getBlockPos)
        .toList()

    private fun taskProcess(
        key: Any,
        level: ServerLevel,
        posDeviceNetworked: BlockPos,
        tip: Boolean,
        processor: (AEKey, () -> Unit, HelperCraftingJob) -> Unit
    ) {
        val unwrapped = when (key) {
            is ItemStack -> AEItemKey.of(key)
            is FluidStack -> AEFluidKey.of(key)
            else -> throw IllegalArgumentException("Cannot unwrap key $key with unknown type ${key.javaClass.name}")
        } as AEKey

        val tile = level.getBlockEntity(posDeviceNetworked)
        if (tile !is IGridConnectedBlockEntity) return

        val serviceCrafting = tile.gridNode?.grid?.craftingService ?: return

        val contextProcess = Tuple<(() -> Unit)?, HelperCraftingJob?>(null, null)
        serviceCrafting.cpus
            .filter(ICraftingCPU::isBusy)
            .forEach { cpu ->
                contextProcess.a = cpu::cancelJob
                contextProcess.b =
                    ((cpu as? CraftingCPUCluster)?.craftingLogic as? AccessorCraftingLogic)?.job as? HelperCraftingJob
                        ?: ((cpu as? AdvCraftingCPU)?.craftingLogic as? AccessorCraftingLogicAdv)?.job as? HelperCraftingJob
                                ?: return@forEach
            }
        val canceler = contextProcess.a ?: return
        val helper = contextProcess.b ?: return
        processor(unwrapped, canceler, helper)

        if (!tip) return
        val player = IPlayerRegistry.getConnected(
            level.server, helper.idPlayer?.toInt() ?: return) ?: return
        PacketDistributor.sendToPlayer(player, CraftingJobStatusPacket(
            helper.link.craftingID,
            helper.outputFinal.what,
            helper.outputFinal.amount,
            helper.amountRemaining,
            CraftingJobStatusPacket.Status.FINISHED
        ))
    }

    private fun AEKey.matches(pattern: IPatternDetails) = this == pattern.primaryOutput.what
}