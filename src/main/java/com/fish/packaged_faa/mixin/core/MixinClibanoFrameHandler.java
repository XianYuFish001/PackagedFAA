package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.mixin.core.overrider.OverriderClibanoFrame;
import com.fish.packaged_faa.mixin.extension.ExtensionClibanoHandler;
import com.stal111.forbidden_arcanus.common.block.clibano.ClibanoCenterBlock;
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoFrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClibanoFrameBlockEntity.class)
public class MixinClibanoFrameHandler extends BlockEntity implements ExtensionClibanoHandler {
    @Unique
    private ExtensionClibanoHandler.HelperHandlerTransfer pfaa$helperTransfer;

    public MixinClibanoFrameHandler(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @Nullable HelperHandlerTransfer getPfaa$helperTransfer() {
        return this.pfaa$helperTransfer;
    }

    @Dynamic(mixin = OverriderClibanoFrame.class)
    @Inject(method = "setLevel", at = @At("RETURN"))
    private void init(Level level, CallbackInfo ci) {
        if (!(this.getBlockState().getBlock() instanceof ClibanoCenterBlock)) return;
        this.pfaa$helperTransfer = new HelperHandlerTransfer(this.worldPosition, level);
    }
}
