@file:Suppress("FunctionName")

package com.fish.packaged_faa.mixin.extension

import com.fish.fishlib.util.extension.tryCast
import com.fish.fishlib.util.extension.unit
import com.fish.packaged_faa.integration.impl.jade.ContainerLog
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager
import org.slf4j.Logger
import java.util.function.Supplier

interface ExtensionManagerRitual {
    fun `pfaa$package`(value: Boolean)

    fun `pfaa$packaged`(): Boolean

    fun `pfaa$bindLogger`(loggerJade: ContainerLog, loggerDebug: Supplier<Logger?>)

    companion object {
        var RitualManager.packaged: Boolean
            get() = this.tryCast<ExtensionManagerRitual>()?.`pfaa$packaged`() ?: false
            set(value) = this.tryCast<ExtensionManagerRitual>()?.`pfaa$package`(value).unit()

        fun RitualManager.bindLogger(loggerJade: ContainerLog, loggerDebug: () -> Logger?) =
            this.tryCast<ExtensionManagerRitual>()?.`pfaa$bindLogger`(loggerJade, loggerDebug)
    }
}