package com.fish.packaged_faa.common.init

import com.fish.fishlib.common.InitObject
import com.fish.fishlib.util.keyBuilder.toKeyPattern
import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.fluid.FluidEssence
import com.fish.packaged_faa.util.UtilKeyBuilder
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

object PFAAFluids {
    @InitObject
    val registerType: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, PackagedFAA.MODID)
    @InitObject
    val registerFluid: DeferredRegister<Fluid> =
        DeferredRegister.create(Registries.FLUID, PackagedFAA.MODID)

    val fluids = mutableListOf<DeferredHolder<Fluid, *>>()

    val typeAureal = regType("type_aureal")
    val typeSouls = regType("type_souls")
    val typeBlood = regType("type_blood")
    val typeExperience = regType("type_experience")

    val fluidAureal = regFluidVirtual("fluid_aureal", typeAureal, FluidEssence.constructor(EssenceType.AUREAL))
    val fluidSouls = regFluidVirtual("fluid_souls", typeSouls, FluidEssence.constructor(EssenceType.SOULS))
    val fluidBlood = regFluidVirtual("fluid_blood", typeBlood, FluidEssence.constructor(EssenceType.BLOOD))
    val fluidExperience = regFluidVirtual("fluid_experience", typeExperience, FluidEssence.constructor(EssenceType.EXPERIENCE))

    private fun regType(name: String): DeferredHolder<FluidType, FluidType> {
        return registerType.register(name) { ->
            FluidType(
                FluidType.Properties.create()
                    .descriptionId(
                        UtilKeyBuilder.of("fluid.type.%s%s".toKeyPattern())
                            .addStr(name)
                            .buildRaw()
                    )
            )
        }
    }

    private fun <T : Fluid> regFluidVirtual(
        name: String, type: Supplier<FluidType>, constructor: (Boolean, BaseFlowingFluid.Properties) -> T
    ): Pair<DeferredHolder<Fluid, T>, DeferredHolder<Fluid, T>> {
        val properties: AtomicReference<BaseFlowingFluid.Properties?> = AtomicReference(null)
        val source = registerFluid.register(name) { ->
            constructor(true, properties.get() as BaseFlowingFluid.Properties)
        }
        val flowing = registerFluid.register(
            name + "flowing"
        ) { -> constructor(false, properties.get() as BaseFlowingFluid.Properties) }
        properties.set(
            BaseFlowingFluid.Properties(
                type, source, flowing
            )
        )
        this.fluids += source
        return source to flowing
    }
}