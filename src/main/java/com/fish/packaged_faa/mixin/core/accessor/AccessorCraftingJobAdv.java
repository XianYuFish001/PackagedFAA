package com.fish.packaged_faa.mixin.core.accessor;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.GenericStack;
import appeng.crafting.CraftingLink;
import appeng.crafting.inv.ListCraftingInventory;
import com.fish.packaged_faa.mixin.helper.HelperCraftingJob;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ExecutingCraftingJob.class)
public interface AccessorCraftingJobAdv extends HelperCraftingJob {
    @Accessor("tasks")
    @Override
    @NotNull Map<@NotNull IPatternDetails, @NotNull HelperJobProgress> getTasks();

    @Accessor("waitingFor")
    @Override
    @NotNull ListCraftingInventory getWaitingFor();

    @Accessor("link")
    @Override
    @NotNull CraftingLink getLink();

    @Accessor("finalOutput")
    @Override
    @NotNull GenericStack getOutputFinal();

    @Accessor("remainingAmount")
    @Override
    long getAmountRemaining();

    @Accessor("playerId")
    @Override
    @Nullable Integer getIdPlayer();

    @Mixin(targets = "net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob$TaskProgress")
    interface AccessorJobProgress extends HelperCraftingJob.HelperJobProgress {
        @Accessor("value")
        @Override
        long getValue();
    }
}
