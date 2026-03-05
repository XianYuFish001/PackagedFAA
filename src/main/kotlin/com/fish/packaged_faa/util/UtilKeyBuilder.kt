package com.fish.packaged_faa.util

import com.fish.fishlib.util.keyBuilder.UtilKeyBuilder
import com.fish.packaged_faa.PackagedFAA

object UtilKeyBuilder : UtilKeyBuilder(PackagedFAA.MODID)
//{
//    const val tooltip = "tooltip.%s%s"
//    const val screenTooltip = "tooltip.screen.%s%s"
//    const val message = "message.%s%s"
//    const val actionBar = "message.actionbar.%s%s"
//    const val screen = "screen.%s%s"
//    const val keywordGroup = "keywordGroup.%s%s"
//    const val config = "%s.configuration%s"
//    const val key = "key.%s%s"
//    const val keyCategory = "key.category.%s%s"
//    const val viewerInfo = "recipe_viewer.info.%s%s"
//    const val viewerTooltip = "recipe_viewer.tooltip.%s%s"
//    const val viewerCategory = "recipe_viewer.category.%s%s"
//    const val essence = "essence.%s%s"
//    const val jadeInfo = "jade.info.%s%s"
//    const val jadeConfig = "config.jade.plugin_%s%s"
//
//    // 一util传三代, 人走util还在
//
//    @JvmStatic
//    fun of(keyTemplate: String): BuilderGeneric<*> {
//        return BuilderGeneric().template(keyTemplate)
//    }
//
//    @JvmStatic
//    fun of(holder: DeferredHolder<*, *>): BuilderGeneric<*> {
//        return BuilderGeneric().item(holder.get())
//    }
//
//    @JvmStatic
//    fun ofDataGen(keyTemplate: String): BuilderDataGen {
//        BuilderDataGen.checkEnvironment()
//        return BuilderDataGen().template(keyTemplate)
//    }
//
//    @JvmStatic
//    fun ofDataGen(holder: DeferredHolder<*, *>): BuilderDataGen {
//        BuilderDataGen.checkEnvironment()
//        return BuilderDataGen().item(holder.get())
//    }
//
//    open class BuilderGeneric<TBuilder : BuilderGeneric<TBuilder>>(val builder: BuilderGeneric<TBuilder>?) {
//        internal var keyTemplate = "%s%s"
//        internal var mainDescription = PackagedFAA.MODID
//        internal var additionalKey = ""
//        internal var args: Array<out Any> = arrayOf()
//
//        internal constructor() : this(null)
//
//        init {
//            this.builder?.let(::copyFrom)
//        }
//
//        @Suppress("UNCHECKED_CAST")
//        private fun unwrap() = this as TBuilder
//
//        internal fun copyFrom(builder: BuilderGeneric<TBuilder>) {
//            this.keyTemplate = builder.keyTemplate
//            this.mainDescription = builder.mainDescription
//            this.additionalKey = builder.additionalKey
//            this.args = builder.args
//        }
//
//        @Suppress("UNCHECKED_CAST")
//        fun bindCollection(target: MutableCollection<Component>) =
//            BuilderCollection(this as BuilderGeneric<BuilderCollection>, target)
//
//        fun newArrayList() = this.bindCollection(ArrayList())
//
//        @Suppress("UNCHECKED_CAST")
//        fun <TKey> bindMap(target: MutableMap<TKey, Component>) =
//            BuilderMap(this as BuilderGeneric<BuilderMap<TKey>>, target)
//
//        fun <TKey> newHashMap() = this.bindMap<TKey>(HashMap())
//
//        fun template(template: String): TBuilder {
//            this.keyTemplate = template
//            return this.unwrap()
//        }
//
//        fun item(obj: Any?): TBuilder {
//            this.mainDescription = when (obj) {
//                is DeferredHolder<*, *> -> return this.item(obj.get())
//                is ItemStack -> return this.item(obj.item)
//                is ItemLike -> obj.asItem().descriptionId.lowercase()
//                is FluidType -> obj.descriptionId.lowercase()
//                else -> PackagedFAA.MODID
//            }
//            return this.unwrap()
//        }
//
//        fun addStr(additionalKey: String): TBuilder {
//            if (!this.additionalKey.endsWith("."))
//                this.additionalKey += "."
//            this.additionalKey += additionalKey
//            return this.unwrap()
//        }
//
//        fun addStr(predicate: Boolean, additionalKey: String): TBuilder {
//            if (predicate) this.addStr(additionalKey)
//            return this.unwrap()
//        }
//
//        fun addStr(predicate: Boolean, keyA: String, keyB: String) =
//            this.addStr(if (predicate) keyA else keyB)
//
//        fun args(vararg args: Any): TBuilder {
//            this.args = args
//            return this.unwrap()
//        }
//
//        fun buildRaw() =
//            String.format(this.keyTemplate, this.mainDescription, this.additionalKey)
//
//        fun build(): MutableComponent =
//            Component.translatable(this.buildRaw(), *this.args)
//    }
//
//    class BuilderDataGen internal constructor(original: BuilderDataGen?) : BuilderGeneric<BuilderDataGen>(original) {
//        internal constructor() : this(null)
//
//        fun branch(additionalKey: String, value: String, locale: String): BuilderDataGen {
//            BuilderDataGen(this)
//                .addStr(additionalKey)
//                .buildInto(value, locale)
//            return this
//        }
//
//        fun branch(additionalKey: String, value: String) =
//            this.branch(additionalKey, value, locale.get())
//
//        fun buildInto(value: String, locale: String) {
//            translators[locale]?.accept(this.buildRaw(), value)
//        }
//
//        fun buildInto(value: String) = this.buildInto(value, locale.get())
//
//        companion object {
//            private val translators: MutableMap<String, BiConsumer<String, String>> = HashMap()
//            private val locale: ThreadLocal<String> = ThreadLocal.withInitial { "en_us" }
//
//            fun bindTranslator(locale: String, translator: BiConsumer<String, String>) {
//                this.checkEnvironment()
//                this.locale.set(locale)
//                this.translators[locale] = translator
//            }
//
//            fun destroy(locale: String) {
//                this.checkEnvironment()
//                this.locale.remove()
//                this.translators.remove(locale)
//            }
//
//            internal fun checkEnvironment() {
//                check(DatagenModLoader.isRunningDataGen())
//                { "Cannot use data-only methods outside of the runData phase" }
//            }
//        }
//    }
//
//    abstract class BuilderSnapshotable<TBuilder : BuilderSnapshotable<TBuilder>> : BuilderGeneric<TBuilder> {
//        internal var snapshot: TBuilder? = null
//
//        internal constructor(original: BuilderGeneric<TBuilder>?) : super(original)
//
//        abstract fun snapshot(): TBuilder
//
//        internal fun restore() {
//            this.snapshot?.let(::copyFrom)
//        }
//    }
//
//    class BuilderCollection internal constructor(
//        original: BuilderGeneric<BuilderCollection>?,
//        private val target: MutableCollection<Component>,
//        saveSnapshot: Boolean = true
//    ) : BuilderSnapshotable<BuilderCollection>(original) {
//        init {
//            if (saveSnapshot)
//                this.snapshot()
//        }
//
//        override fun snapshot(): BuilderCollection {
//            this.snapshot = BuilderCollection(this, this.target, false)
//            return this
//        }
//
//        fun buildInto(): BuilderCollection {
//            this.target.add(this.build())
//            this.restore()
//            return this
//        }
//
//        fun buildInto(additionalKey: String): BuilderCollection {
//            this.target.add(this.addStr(additionalKey).build())
//            this.restore()
//            return this
//        }
//
//        fun get(): Collection<Component> = this.target
//    }
//
//    class BuilderMap<TKey> internal constructor(
//        original: BuilderGeneric<BuilderMap<TKey>>?,
//        private val target: MutableMap<TKey, Component>,
//        saveSnapshot: Boolean = true
//    ) : BuilderSnapshotable<BuilderMap<TKey>>(original) {
//        init {
//            if (saveSnapshot)
//                this.snapshot()
//        }
//
//        override fun snapshot(): BuilderMap<TKey> {
//            this.snapshot = BuilderMap(this, this.target, false)
//            return this
//        }
//
//        /** 不建议真的在非string的情况下调用🤓 */
//        fun buildInto(key: TKey): BuilderMap<TKey> {
//            this.target[key] = this.addStr(key.toString()).build()
//            this.restore()
//            return this
//        }
//
//        fun buildIntoPlain(key: TKey): BuilderMap<TKey> {
//            this.target[key] = this.build()
//            this.restore()
//            return this
//        }
//
//        fun get(): Map<TKey, Component> = this.target
//    }
//}