package com.fish.packaged_faa.mixin.extension

import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager

interface ExtensionManagerRitual {
    fun `pfaa$package`(value: Boolean)

    fun `pfaa$packaged`(): Boolean

    companion object {
        var RitualManager.packaged: Boolean
            get() = (this as? ExtensionManagerRitual)?.`pfaa$packaged`() ?: false
            set(value) = (this as? ExtensionManagerRitual)?.`pfaa$package`(value) as Unit
    }
}