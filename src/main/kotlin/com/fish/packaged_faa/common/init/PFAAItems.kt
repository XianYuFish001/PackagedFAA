package com.fish.packaged_faa.common.init

import com.fish.packaged_faa.PackagedFAA
import com.fish.packaged_faa.common.registry.item.ItemHephaestusPackaged
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object PFAAItems {
    val register: DeferredRegister.Items = DeferredRegister.createItems(PackagedFAA.MODID)

    val hephaestusPackaged: DeferredItem<Item> = register.register("hephaestus_packaged", ::ItemHephaestusPackaged)
    val pedestalPackaged: DeferredItem<Item> = register.register("pedestal_packaged", Supplier {
        BlockItem(PFAABlocks.pedestalPackaged.get(), Item.Properties())
    })

//    val bucketAureal = this.regBucketItem("bucket_aureal", PFAAFluids.fluidAureal.first)
//    val bucketBlood = this.regBucketItem("bucket_blood", PFAAFluids.fluidBlood.first)
//    val bucketSouls = this.regBucketItem("bucket_souls", PFAAFluids.fluidSouls.first)
//    val bucketExperience = this.regBucketItem("bucket_experience", PFAAFluids.fluidExperience.first)
//
//    fun regBucketItem(name: String, fluid: DeferredHolder<Fluid, *>): DeferredItem<BucketItem> {
//        return register.register(name, Supplier {
//            BucketItem(fluid.get(), Item.Properties()
//                .stacksTo(1)
//                .craftRemainder(Items.BUCKET))
//        })
//    }
}