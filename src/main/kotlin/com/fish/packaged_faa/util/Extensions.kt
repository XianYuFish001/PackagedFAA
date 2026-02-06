package com.fish.packaged_faa.util

import net.minecraft.world.item.ItemStack

fun List<ItemStack>.flatStack(lengthMax: Int = Int.MAX_VALUE): List<ItemStack> {
    val flattened = ArrayList<ItemStack>()
    this.forEach {
        if (it.isEmpty) return@forEach
        for (i in 0..<it.count)
            flattened.add(it.copyWithCount(1))
    }
    return if (flattened.size > lengthMax)
        flattened.subList(0, lengthMax)
    else
        flattened
}

fun <T> List<T>.replace(indexReplacement: Int, replacement: T): List<T> {
    if (indexReplacement >= this.size) return this
    val newList = this.toMutableList()
    newList[indexReplacement] = replacement
    return newList
}

fun ItemStack.orElse(default: ItemStack): ItemStack {
    return if (this.isEmpty) default else this
}

fun <T> List<T>.ifNotEmpty(action: (List<T>) -> Any?): List<T> {
    if (this.isNotEmpty()) action(this)
    return this
}