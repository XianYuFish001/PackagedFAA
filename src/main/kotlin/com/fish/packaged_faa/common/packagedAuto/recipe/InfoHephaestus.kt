package com.fish.packaged_faa.common.packagedAuto.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import thelm.packagedauto.api.IPackagePattern
import thelm.packagedauto.api.IPackageRecipeInfo
import thelm.packagedauto.recipe.IPositionedProcessingPackageRecipeInfo
import thelm.packagedauto.util.MiscHelper
import thelm.packagedauto.util.PackagePattern

class InfoHephaestus(inputMain: ItemStack, inputs: List<ItemStack>, private val output: ItemStack) : IPositionedProcessingPackageRecipeInfo {
    private val inputs = ArrayList<ItemStack>(8)
    private val outputs = listOf(this.output)
    private val patterns = ArrayList<IPackagePattern>()
    private val matrix = Int2ObjectOpenHashMap<ItemStack>(9)

    init {
        inputs
            .mapIndexed(::Pair)
            .toMap()
            .filter { !it.value.isEmpty }
            .forEach { (index, stack) ->
                this.inputs += stack
                this.matrix[index] = stack
            }

        this.inputs.addFirst(inputMain)
        this.matrix[TypeHephaestus.slotForge] = inputMain

        var indexPart = 0
        while (indexPart * 9 < this.inputs.size) {
            this.patterns.add(PackagePattern(this, indexPart, true))
            ++indexPart
        }
    }

    override fun getRecipeType() = TypeHephaestus.instance

    override fun isValid() = this.inputs.size > 1

    override fun getPatterns() = this.patterns

    override fun getInputs() = this.inputs

    override fun getOutputs() = this.outputs

    override fun getMatrix() = this.matrix

    override fun getEncoderStacks(): Int2ObjectMap<ItemStack> {
        val mapStacks = Int2ObjectOpenHashMap<ItemStack>()

        mapStacks[TypeHephaestus.slotForge] = this.inputs[0]

        val iteratorPedestal = TypeHephaestus.slotsPedestal.iterator()
        this.inputs.subList(1, this.inputs.size).forEach {
            if (!iteratorPedestal.hasNext()) return@forEach
            mapStacks[iteratorPedestal.nextInt()] = it
        }

        mapStacks[81] = this.output

        return mapStacks
    }

    override fun equals(other: Any?): Boolean {
        return MiscHelper.INSTANCE.recipeEquals(
            this,
            null,
            other as? IPackageRecipeInfo ?: return false,
            null
        )
    }
    override fun hashCode() = MiscHelper.INSTANCE.recipeHashCode(this, null)

    companion object {
        val mapCodec: MapCodec<InfoHephaestus> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ItemStack.OPTIONAL_CODEC.orElse(ItemStack.EMPTY).sizeLimitedListOf(81).fieldOf("inputs").forGetter { it.inputs },
                ItemStack.OPTIONAL_CODEC.orElse(ItemStack.EMPTY).fieldOf("output").forGetter { it.output }
            ).apply(instance) { inputs, output -> InfoHephaestus(inputs[0], inputs.subList(1, inputs.size), output) }
        }

        val codec: Codec<InfoHephaestus> = mapCodec.codec()

        val streamCodec: StreamCodec<RegistryFriendlyByteBuf, InfoHephaestus> = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, { it.inputs },
            ItemStack.OPTIONAL_STREAM_CODEC, { it.output }
        ) { inputs, output -> InfoHephaestus(inputs[0], inputs.subList(1, inputs.size), output) }
    }
}