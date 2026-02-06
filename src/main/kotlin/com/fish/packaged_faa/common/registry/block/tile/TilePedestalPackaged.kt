package com.fish.packaged_faa.common.registry.block.tile

import com.fish.packaged_faa.common.init.PFAATiles
import com.stal111.forbidden_arcanus.common.block.entity.PedestalBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TilePedestalPackaged(pos: BlockPos, state: BlockState) : PedestalBlockEntity(pos, state) {
    internal var tileForge: TileHephaestusPackaged? = null

    internal var stack: ItemStack = ItemStack.EMPTY
        set(value) {
            field = value
            this.setChanged()
            this.level?.sendBlockUpdated(this.blockPos, this.blockState, this.blockState, Block.UPDATE_CLIENTS)
        }

    override fun getStack() = this.stack

    override fun onLoad() {
        super.onLoad()
        this.searchForge()
    }

    private fun clearForge() {
        this.tileForge?.pedestals?.remove(this)
        this.tileForge = null
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        this.clearForge()
    }

    private fun searchForge() {
        if (!(this.tileForge?.isRemoved ?: true)) return
        val tiles = BlockPos.betweenClosed(
            this.blockPos.offset(-3, 0, -3),
            this.blockPos.offset(3, 0, 3))
            .mapNotNull { this.level?.getBlockEntity(it) }
            .filterIsInstance<TileHephaestusPackaged>()
        if (tiles.isEmpty()) return
        this.tileForge = tiles[0]
        this.tileForge?.pedestals?.add(this)
    }

    override fun setRemoved() {
        super.setRemoved()
        this.clearForge()
    }

    override fun saveAdditional(data: CompoundTag, lookupProvider: HolderLookup.Provider) {
        super.saveAdditional(data, lookupProvider)
        if (this.stack.isEmpty) return
        data.put("stack", this.stack.save(lookupProvider))
    }

    override fun loadAdditional(data: CompoundTag, lookupProvider: HolderLookup.Provider) {
        super.loadAdditional(data, lookupProvider)
        if (!data.contains("stack")) this.stack = ItemStack.EMPTY
        else this.stack = ItemStack.parse(lookupProvider, data.getCompound("stack"))
            .orElse(ItemStack.EMPTY)
    }

    override fun getType(): BlockEntityType<*> = PFAATiles.pedestalPackaged.get()
}