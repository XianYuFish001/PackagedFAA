package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.PFAAConfig;
import com.fish.packaged_faa.common.init.PFAAFluids;
import com.fish.packaged_faa.common.registry.fluid.FluidEssence;
import com.fish.packaged_faa.mixin.core.overrider.OverriderCrystalArcane;
import com.fish.packaged_faa.mixin.extension.ExtensionTankCrystal;
import com.stal111.forbidden_arcanus.common.block.entity.ArcaneCrystalObeliskBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ArcaneCrystalObeliskBlockEntity.class)
public class MixinCrystalArcane extends BlockEntity implements ExtensionTankCrystal {
    @Unique
    private final FluidTank pfaa$tankAureal = new FluidTank(16000,
            stack -> stack.is(PFAAFluids.INSTANCE.getTypeAureal().get())) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public MixinCrystalArcane(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @NotNull FluidTank pfaa$getTankAureal() {
        return this.pfaa$tankAureal;
    }

    @Dynamic(mixin = OverriderCrystalArcane.class)
    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        var dataTank = this.pfaa$tankAureal.writeToNBT(registries, new CompoundTag());
        tag.put("tank", dataTank);
    }

    @Dynamic(mixin = OverriderCrystalArcane.class)
    @Inject(method = "loadAdditional", at = @At("RETURN"))
    private void load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (!tag.contains("tank")) return;
        var dataTank = tag.getCompound("tank");
        this.pfaa$tankAureal.readFromNBT(registries, dataTank);
    }

    @Redirect(method = "serverTick", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static <T extends BlockEntity> void increaseEssence(Optional<T> instance,
                                                                Consumer<? super T> action,
                                                                Level level,
                                                                BlockPos pos,
                                                                BlockState state,
                                                                ArcaneCrystalObeliskBlockEntity blockEntity) {
        if (instance.isPresent()) {
            action.accept(instance.get());
            return;
        }

        var value = PFAAConfig.INSTANCE.getFactorAureal();
        if (value == 0) return;
        ExtensionTankCrystal.Companion.getTankAureal(blockEntity).fill(
                FluidEssence.Companion.toStack(EssenceType.AUREAL, value),
                IFluidHandler.FluidAction.EXECUTE);
    }
}
