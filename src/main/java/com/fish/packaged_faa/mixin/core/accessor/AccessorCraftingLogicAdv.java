package com.fish.packaged_faa.mixin.core.accessor;

import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvCraftingCPULogic.class)
public interface AccessorCraftingLogicAdv {
    @Accessor("job")
    ExecutingCraftingJob getJob();
}
