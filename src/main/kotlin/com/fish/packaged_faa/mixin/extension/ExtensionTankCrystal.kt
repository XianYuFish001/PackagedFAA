package com.fish.packaged_faa.mixin.extension

import com.stal111.forbidden_arcanus.common.block.entity.ArcaneCrystalObeliskBlockEntity
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

interface ExtensionTankCrystal {
    fun `pfaa$getTankAureal`(): FluidTank

    companion object {
        val ArcaneCrystalObeliskBlockEntity.tankAureal: FluidTank
            get() = (this as ExtensionTankCrystal).`pfaa$getTankAureal`()
    }
}