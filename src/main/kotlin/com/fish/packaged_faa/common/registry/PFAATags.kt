package com.fish.packaged_faa.common.registry

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.FluidTags
import net.minecraft.tags.TagKey
import net.neoforged.neoforge.common.Tags

object PFAATags {
    object Fluid {
        val experience = Tags.Fluids.EXPERIENCE
        val blood = this.create("blood")
        val aureal = this.create("aureal")
        val souls = this.create("souls")
        val essences = listOf(experience, blood, aureal, souls)

        private fun create(name: String) =
            FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name))
    }

    fun <T> matches(matcher: (TagKey<T>) -> Boolean, vararg tags: TagKey<T>) = tags.any(matcher)
}