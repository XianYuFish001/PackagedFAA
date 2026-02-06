package com.fish.packaged_faa.common.init

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.block.tile.TileHephaestusPackaged
import com.fish.packaged_faa.common.registry.block.tile.TilePedestalPackaged
import com.fish.packaged_faa.common.registry.block.tile.TileTransmuterUtremJar
import com.stal111.forbidden_arcanus.core.init.ModBlocks
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object PFAATiles {
    val register: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PackagedFAA.MODID)

    val hephaestusPackaged = register("hephaestus_packaged", ::TileHephaestusPackaged, PFAABlocks.hephaestusPackaged)
    val pedestalPackaged = register("pedestal_packaged", ::TilePedestalPackaged, PFAABlocks.pedestalPackaged)

    val transmuterUtremJar = register.register("transmuter_utrem_jar", Supplier {
        BlockEntityType.Builder
            .of(::TileTransmuterUtremJar, ModBlocks.UTREM_JAR.get())
            .build(null)
    })

    private fun <T : BlockEntity> register(
        name: String, factory: BlockEntitySupplier<T>, block: DeferredBlock<*>
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> = register.register<BlockEntityType<T>>(
        name,
        Supplier {
            BlockEntityType.Builder
                .of<T>(factory, block.get())
                .build(null)
        }
    )
}