package com.fish.packaged_faa.mixin.core.accessor;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.GenericStack;
import appeng.crafting.CraftingLink;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;
import com.fish.packaged_faa.mixin.helper.HelperCraftingJob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ExecutingCraftingJob.class)
public interface AccessorCraftingJob extends HelperCraftingJob {
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

    @Mixin(targets = "appeng.crafting.execution.ExecutingCraftingJob$TaskProgress")
    interface AccessorJobProgress extends HelperJobProgress {
        @Accessor("value")
        @Override
        long getValue();
    }
}
