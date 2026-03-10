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

        private fun create(name: String): TagKey<net.minecraft.world.level.material.Fluid> =
            FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name))
    }

    fun <T> matches(vararg tags: TagKey<T>, matcher: (TagKey<T>) -> Boolean) = tags.any(matcher)
}