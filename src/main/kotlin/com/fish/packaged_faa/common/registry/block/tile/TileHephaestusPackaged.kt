package com.fish.packaged_faa.common.registry.block.tile

import com.fish.fishlib.util.extension.invoke
import com.fish.fishlib.util.extension.onlyIf
import com.fish.fishlib.util.extension.orElseGet
import com.fish.packaged_faa.PFAAConfig
import com.fish.packaged_faa.common.init.PFAABlocks
import com.fish.packaged_faa.common.init.PFAAItems
import com.fish.packaged_faa.common.init.PFAATiles
import com.fish.packaged_faa.common.packagedAuto.recipe.InfoHephaestus
import com.fish.packaged_faa.common.registry.PFAATags
import com.fish.packaged_faa.common.registry.block.BlockHephaestusPackaged
import com.fish.packaged_faa.common.registry.fluid.FluidEssence
import com.fish.packaged_faa.integration.helper.ManagerIntegration
import com.fish.packaged_faa.integration.impl.jade.ContainerLog
import com.fish.packaged_faa.integration.impl.point.IntegrationAE
import com.fish.packaged_faa.mixin.extension.ExtensionManagerRitual.Companion.bindLogger
import com.fish.packaged_faa.mixin.extension.ExtensionManagerRitual.Companion.packaged
import com.fish.packaged_faa.util.UtilKeyBuilder
import com.fish.packaged_faa.util.flatStack
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock
import com.stal111.forbidden_arcanus.common.block.entity.forge.ForgeDataCache
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity
import com.stal111.forbidden_arcanus.common.block.entity.forge.circle.MagicCircleController
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssencesDefinition
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssencesStorage
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ValidRitualIndicator
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerDefinition
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.ItemHandlerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import thelm.packagedauto.api.IPackageCraftingMachine
import thelm.packagedauto.api.IPackageRecipeInfo
import thelm.packagedauto.block.PackagedAutoBlocks
import thelm.packagedauto.block.entity.BaseBlockEntity
import thelm.packagedauto.inventory.BaseItemHandler
import thelm.packagedauto.util.MiscHelper
import kotlin.jvm.optionals.getOrNull

class TileHephaestusPackaged(pos: BlockPos, state: BlockState) : BaseBlockEntity(
    PFAATiles.hephaestusPackaged.get(), pos, state
), IPackageCraftingMachine {
    // Server
    private var cacheData = ForgeDataCache(ArrayList(), ItemStack.EMPTY, ArrayList(8))
    private var storageEssence = EssencesStorage()
    private val handlerItem = BaseItemHandler(this, 9)

    internal val pedestals: MutableSet<TilePedestalPackaged> = HashSet()

    private var recipe: InfoHephaestus? = null
    private var working = false
    private var levelForge = BlockHephaestusPackaged.getLevelForge(state)
        set(value) {
            field = value
            this.managerRitual.setForgeTier(value.asInt)
            this.level?.setBlock(
                this.blockPos,
                this.blockState.setValue(
                    BlockHephaestusPackaged.propertyLevelForge,
                    value.asInt
                ),
                Block.UPDATE_CLIENTS
            )
        }

    val containerLog = ContainerLog("hephaestus_packaged")

    // Client
    val controllerCircle: MagicCircleController = MagicCircleController(2)
    var counterDisplay = 0
        private set
    var indicatorRitual: ValidRitualIndicator? = null
        private set
    var durationRitual = 0
        private set

    // Both
    val managerRitual: RitualManager =
        RitualManager(this.controllerCircle, this.levelForge.asInt, this.cacheData)
    var inputMain: ItemStack = ItemStack.EMPTY
        private set

    init {
        this.managerRitual.setForgeTier(this.levelForge.asInt)
        this.managerRitual.packaged = true
        this.managerRitual.bindLogger(this.containerLog) { Logger }
    }

    override fun setLevel(level: Level) {
        super.setLevel(level)
        this.managerRitual.setup(level as? ServerLevel ?: return, this.blockPos)
    }

    fun tickClient() {
        this.controllerCircle.tick()
        this.indicatorRitual?.tick()
        this.counterDisplay++
    }

    fun tickServer() {
        val result = this.managerRitual.tick().getOrNull() ?: return

        this.working = false
        this.recipe = null
        this.cacheData = ForgeDataCache(
            ArrayList(),
            ItemStack.EMPTY,
            this.cacheData.enhancers()
        )

        this.pedestals.forEach { it.stack = ItemStack.EMPTY }

        if (result.isEmpty) return
        if (result.item is BlockItem) {
            val block = (result.item as BlockItem).block
            if (block is HephaestusForgeBlock) {
                this.levelForge = block.level

                val ae = ManagerIntegration<IntegrationAE>() ?: return

                val level = this.level as? ServerLevel ?: return
                val devices = ae.findDevice(this.blockPos, level).ifEmpty { return }
                ae.cancelTaskInWaiting(result, level, devices[0])

                return
            }
        }

        ItemHandlerHelper.insertItem(this.handlerItem, result, false)
        this.ejectItem()

        this.setChanged()
        this.level?.sendBlockUpdated(
            this.blockPos,
            this.blockState,
            this.blockState,
            Block.UPDATE_CLIENTS
        )
    }

    private fun ejectItem() {
        if (this.isBusy) return
        Direction.entries.forEach { side ->
            val posTarget = this.blockPos.relative(side)

            val block = this.level?.getBlockState(posTarget) ?: return@forEach
            if (block.`is`(PackagedAutoBlocks.PACKAGER)) return@forEach

            val handlerItem = this.level!!.getCapability(
                Capabilities.ItemHandler.BLOCK,
                posTarget,
                side.opposite
            ) ?: return@forEach

            for (indexSlot in 0 until this.handlerItem.slots) {
                val stack = this.handlerItem.getStackInSlot(indexSlot)
                if (stack.isEmpty) continue
                val remainder = ItemHandlerHelper.insertItem(handlerItem, stack, false)
                this.handlerItem.setStackInSlot(indexSlot, remainder)
            }
        }
    }

    fun drop(result: MutableSet<ItemStack>) {
        this.cacheData.enhancers()
            .map { it.value().displayItem.value().defaultInstance }
            .forEach(result::add)


        this.recipe ?: return

        result.add(this.inputMain.copy())
        this.pedestals
            .map { it.stack.copy() }
            .forEach(result::add)
    }

    override fun onLoad() {
        super.onLoad()
        BlockPos.betweenClosed(
            this.blockPos.offset(-3, 0, -3),
            this.blockPos.offset(3, 0, 3)
        )
            .mapNotNull { this.level?.getBlockEntity(it) }
            .filterIsInstance<TilePedestalPackaged>()
            .filter { it.tileForge == null }
            .forEach {
                it.tileForge = this
                this.pedestals.add(it)
            }
    }

    override fun setRemoved() {
        this.pedestals.forEach {
            it.tileForge = null
            it.stack = ItemStack.EMPTY
        }
        this.pedestals.clear()
        super.setRemoved()
    }

    fun essences(): EssencesDefinition = this.storageEssence.immutable()

    fun enhancers(): List<Holder<EnhancerDefinition>> = this.cacheData.enhancers()

    private fun toggleIndicator(enabled: Boolean) {
        this.indicatorRitual = if (enabled) ValidRitualIndicator(true) else null
    }

    private fun saveChanges(registries: HolderLookup.Provider) =
        this.managerRitual.onDataChanged(
            this.cacheData,
            this.storageEssence.immutable(),
            registries
        )

    override fun triggerEvent(id: Int, type: Int): Boolean {
        when (id) {
            HephaestusForgeBlockEntity.UPDATE_MAGIC_CIRCLE ->
                this.controllerCircle.handleEvent(this.level, this.blockPos, type)

            HephaestusForgeBlockEntity.UPDATE_RITUAL_INDICATOR ->
                this.toggleIndicator(type == 1)

            HephaestusForgeBlockEntity.UPDATE_RITUAL_DURATION ->
                this.durationRitual = type

            else -> return super.triggerEvent(id, type)
        }
        return true
    }

    fun putEnhancer(stack: ItemStack): Boolean {
        if (this.level?.isClientSide ?: true) return false

        val enhancer = EnhancerHelper.getEnhancerHolder(this.level!!.registryAccess(), stack)
        if (enhancer.isEmpty) return false

        if (this.cacheData.enhancers().contains(enhancer.get())) return false
        this.cacheData.enhancers().add(enhancer.get())
        this.setChanged()
        return true
    }

    override fun acceptPackage(
        infoRecipe: IPackageRecipeInfo,
        stacks: List<ItemStack>,
        direction: Direction
    ): Boolean {
        if (infoRecipe !is InfoHephaestus) return false
        if (this.isBusy || this.recipe != null) return false

        if (infoRecipe.outputs[0].item == PFAAItems.hephaestusPackaged()) return false

        if (!this.placeStack(stacks)) return false
        if (!this.updateRitual()) {
            Logger?.debug("[Check 2/4] First try failed")
            this.containerLog.warn("1")

            this.collectEssence()
            if (!this.updateRitual()) {
                Logger?.debug("[Check 4/4] Second try failed")
                this.containerLog.warn("3")

                this.pedestals.forEach { it.stack = ItemStack.EMPTY }
                return false
            }
        }

        this.working = true
        this.recipe = infoRecipe
        this.containerLog.idle()

        this.setChanged()
        this.level?.sendBlockUpdated(
            this.blockPos,
            this.blockState,
            this.blockState,
            Block.UPDATE_CLIENTS
        )

        return true
    }

    private fun placeStack(stacks: List<ItemStack>): Boolean {
        this.inputMain = stacks[0]
        val stacks = stacks.subList(1, stacks.size).flatStack()
        if (stacks.size > this.pedestals.size) {
            Logger?.debug("[Check 1/4][Precheck] No enough pedestals({}/{})", this.pedestals.size, stacks.size)
            this.containerLog.error(
                "0", "pre",
                args = listOf(
                    this.pedestals.size.toString(),
                    stacks.size.toString()
                )
            )
            return false
        }

        this.cacheData = ForgeDataCache(
            ArrayList(),
            this.inputMain,
            this.cacheData.enhancers()
        )
        val iteratorPedestals = this.pedestals.iterator()
        stacks.forEach { stack ->
            if (!iteratorPedestals.hasNext()) {
                Logger?.debug("[Check 1/4][Executing] No enough pedestals")
                this.containerLog.error("0", "exec")
                return false
            }
            val pedestal = iteratorPedestals.next()
            this.cacheData.setIngredient(pedestal.blockPos, stack)
            pedestal.stack = stack
        }
        return true
    }

    private fun collectEssence() = Direction.entries.forEach { side ->
        val posTarget = this.blockPos.relative(side)

        val handlerFluid = this.level?.getCapability(
            Capabilities.FluidHandler.BLOCK,
            posTarget,
            side.opposite
        ).orElseGet {
            Logger?.debug("[Check 3/4][Precheck] Side {} with no fluidHandler", side.serializedName)
            this.containerLog.warn("2", "pre", args = listOf(side.serializedName))
            return@forEach
        }

        val drained = ArrayList<FluidStack>()
        for (indexSlot in 0 until handlerFluid.tanks) {
            val stack = handlerFluid.getFluidInTank(indexSlot)
            if (stack.isEmpty) {
                Logger?.debug("[Check 3/4][Executing] Tank {} is empty", indexSlot)
                this.containerLog.warn("2", "exec", "empty", args = listOf(indexSlot.toString()))
                continue
            }
            if (PFAATags.Fluid.essences.none(stack::`is`)) {
                Logger?.debug("[Check 3/4][Executing] Tank {} with wrong fluid", indexSlot)
                this.containerLog.warn("2", "exec", "wrong", args = listOf(indexSlot.toString()))
                continue
            }
            if (
                (this.storageEssence[
                    FluidEssence
                        .fromStack(stack, false)
                        ?.first
                        ?: continue
                ] ?: 0) >= LIMIT_ESSENCES
            ) continue
            drained.add(handlerFluid.drain(stack, IFluidHandler.FluidAction.EXECUTE))
            Logger?.debug("[Check 3/4][Executing] Drained {}", drained.lastOrNull())
        }
        FluidEssence.fromStack(drained, this.storageEssence, true)
    }

    private fun updateRitual(): Boolean {
        this.level?.registryAccess()?.let { this.saveChanges(it) }

        val player = this.level?.getPlayerByUUID(this.ownerUUID.orElseGet {
            Logger?.debug("[Check (2&4)/4][Precheck] Null uuid")
            this.containerLog.error("1", "pre", "uuid")
            return false
        }) as? ServerPlayer ?: return false
        return this.managerRitual.startRitual(player, this.storageEssence)
    }

    override fun getComparatorSignal(): Int {
        return if (this.isBusy)
            1
        else if (!this.itemHandler.getStacks().all { !it.isEmpty })
            15
        else
            0
    }

    override fun saveAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(data, registries)
        this.managerRitual.save(CompoundTag(), registries)
        EssencesStorage.CODEC.encodeStart(NbtOps.INSTANCE, this.storageEssence)
            .ifSuccess { data.put("essence", it) }

        val opsRegistry = registries.createSerializationContext(NbtOps.INSTANCE)
        ForgeDataCache.CODEC.encodeStart(opsRegistry, this.cacheData)
            .ifSuccess { data.put("cache", it) }

        this.working = this.managerRitual.isRitualActive

        this.recipe?.let {
            data.put("recipe", MiscHelper.INSTANCE.saveRecipe(CompoundTag(), it, registries))
        }
    }

    override fun loadAdditional(data: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(data, registries)
        this.managerRitual.load(data, registries)
        EssencesStorage.CODEC.parse(NbtOps.INSTANCE, data.get("essence")).ifSuccess { this.storageEssence = it }

        val opsRegistry = registries.createSerializationContext(NbtOps.INSTANCE)
        ForgeDataCache.CODEC.parse(opsRegistry, data.get("cache")).ifSuccess {
            this.cacheData = ForgeDataCache(
                it.cachedIngredients(),
                it.mainIngredient(),
                ArrayList(it.enhancers())
            )
        }

        this.recipe = null
        data.get("recipe")?.let {
            MiscHelper.INSTANCE.loadRecipe(it as CompoundTag, registries)
        }?.let {
            if (it !is InfoHephaestus) return@let
            this.recipe = it
        }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val data = super.getUpdateTag(registries)

        val inputMain = this.recipe?.inputs[0] ?: ItemStack.EMPTY
        if (inputMain.isEmpty) return data

        data.put("input_main", inputMain.save(registries))
        return data
    }

    override fun handleUpdateTag(data: CompoundTag, registries: HolderLookup.Provider) {
        super.handleUpdateTag(data, registries)

        this.inputMain = if (!data.contains("input_main")) ItemStack.EMPTY
        else ItemStack.parseOptional(registries, data.getCompound("input_main"))
    }

    override fun onDataPacket(
        connection: Connection, packet: ClientboundBlockEntityDataPacket, registries: HolderLookup.Provider
    ) = this.handleUpdateTag(packet.tag, registries)

    override fun isBusy() = this.working || this.managerRitual.isRitualActive

    override fun getDefaultName() = UtilKeyBuilder.of(PFAABlocks.hephaestusPackaged).build()

    override fun createMenu(p0: Int, p1: Inventory, p2: Player) = null

    companion object {
        private const val LIMIT_ESSENCES = 32768

        private var Logger: Logger? = LoggerFactory.getLogger("PFAA/HephaestusPackaged")
            get() = field.onlyIf { PFAAConfig.LoggedHephaestus }
    }
}