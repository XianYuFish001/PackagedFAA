package com.fish.packaged_faa.integration.impl.point

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

interface IntegrationAE {
    fun cancelTask(key: Any, level: ServerLevel, posDeviceNetworked: BlockPos, tip: Boolean = true)

    fun cancelTaskInWaiting(key: Any, level: ServerLevel, posDeviceNetworked: BlockPos, tip: Boolean = true)

    fun findDevice(pos: BlockPos, level: ServerLevel): List<BlockPos>
}