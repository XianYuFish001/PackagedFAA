package com.fish.packaged_faa.common.registry.fluid

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.init.PFAAFluids
import com.fish.packaged_faa.common.registry.PFAATags
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssencesStorage
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.registries.DeferredHolder

class FluidEssence(source: Boolean, properties: Properties, val type: EssenceType) : FluidVirtual(source, properties) {
    class ExtensionClientFluid(private val path: String) : IClientFluidTypeExtensions {
        override fun getTintColor() = 0xFFFFFF
        override fun getStillTexture(): ResourceLocation = PackagedFAA.getLocation("block/${this.path}")
    }

    companion object {
        fun constructor(type: EssenceType): (Boolean, Properties) -> FluidEssence =
            { source, properties -> FluidEssence(source, properties, type) }

        fun toStack(storage: EssencesStorage): List<FluidStack> = storage.map { (type, amount) ->
            FluidStack(this.convertTo(type), amount)
        }

        fun toStack(type: EssenceType, amount: Int) = FluidStack(this.convertTo(type), amount)

        fun stacks(): List<FluidStack> = EssenceType.entries.map {
            FluidStack(this.convertTo(it), 32768)
        }

        fun fromStack(stacks: List<FluidStack>, storage: EssencesStorage, consume: Boolean) = stacks.forEach { stack ->
            val indexSlot = (stack.fluid as? FluidEssence)?.type ?: this.convertFrom(stack) ?: return@forEach
            storage[indexSlot] = (storage[indexSlot] ?: 0) + stack.amount
            if (consume) stack.amount = 0
        }

        fun fromStack(stack: FluidStack, consume: Boolean): Pair<EssenceType, Int>? {
            val type = (stack.fluid as? FluidEssence)?.type
                ?: this.convertFrom(stack) ?: return null
            val result = Pair(type, stack.amount)
            if (consume) stack.amount = 0
            return result
        }

        fun convertFrom(stack: FluidStack): EssenceType? {
            try {
                return EssenceType.valueOf(
                    PFAATags.Fluid.essences
                        .find(stack::`is`)
                        ?.location
                        ?.path
                        ?.uppercase()
                        ?: return null
                )
            } catch (exception: IllegalArgumentException) {
                return null
            }
        }

        fun convertTo(type: EssenceType): DeferredHolder<Fluid, FluidEssence> =
            when (type) {
                EssenceType.AUREAL -> PFAAFluids.fluidAureal.first
                EssenceType.BLOOD -> PFAAFluids.fluidBlood.first
                EssenceType.SOULS -> PFAAFluids.fluidSouls.first
                EssenceType.EXPERIENCE -> PFAAFluids.fluidExperience.first
            }
    }
}