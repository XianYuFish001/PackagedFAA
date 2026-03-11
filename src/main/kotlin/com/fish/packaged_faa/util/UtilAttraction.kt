package com.fish.packaged_faa.util

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity

/**
 * @author Team-EnderIO
 */
object UtilAttraction {
    /**
     * Pulls the entity closer to the given BlockPos. Once it is in inside the collision distance, the function will return true, otherwise it returns false.
     */
    fun moveToPos(entity: Entity, pos: BlockPos, speedVertical: Double, speedHorizontal: Double, collisionDistanceSq: Double) =
        moveToPos(
            entity,
            pos.x + 0.5,
            pos.y + 0.5,
            pos.z + 0.5,
            speedVertical,
            speedHorizontal,
            collisionDistanceSq
        )

    /**
     * Pulls the entity closer to the given x, y and z coordinates. Once it is in inside the collision distance, the function will return true, otherwise it returns false.
     */
    fun moveToPos(
        entity: Entity,
        x: Double,
        y: Double,
        z: Double,
        speedVertical: Double,
        speedHorizontal: Double,
        collisionDistanceSq: Double
    ): Boolean {
        var x = x
        var y = y
        var z = z
        if (entity.isRemoved) { //If the entity no longer exists, return false
            return false
        }
        x -= entity.x
        y -= entity.y
        z -= entity.z

        val distanceSq = x * x + y * y + z * z

        if (distanceSq < collisionDistanceSq) {
            return true
        } else {
            val adjustedSpeed = speedHorizontal / distanceSq
            val mov = entity.deltaMovement
            val deltaX = mov.x + x * adjustedSpeed
            val deltaZ = mov.z + z * adjustedSpeed
            val deltaY = if (y > 0) {
                //if entity is below, raise them to target level at a fixed rate
                0.12
            } else {
                //Scaling y speed based on distance works poorly due to 'gravity' so use fixed speed
                mov.y + y * speedVertical
            }
            entity.setDeltaMovement(deltaX, deltaY, deltaZ)
            return false
        }
    }
}
