package com.fish.packaged_faa.mixin.core;

import com.fish.fishlib.util.extension.ExtensionStdKt;
import com.fish.packaged_faa.PFAAConfig;
import com.fish.packaged_faa.mixin.extension.ExtensionJarEssence;
import com.stal111.forbidden_arcanus.common.block.entity.EssenceUtremJarBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import com.stal111.forbidden_arcanus.core.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Mixin(EssenceUtremJarBlockEntity.class)
public abstract class MixinJarEssence extends BlockEntity
        implements GameEventListener.Provider<ExtensionJarEssence.ListenerJar>, ExtensionJarEssence {
    @Shadow
    public abstract void addEssence(int amount);

    @Shadow
    private int amount;
    @Shadow
    private int limit;
    @Unique
    private ExtensionJarEssence.ListenerJar pfaa$listener;
    @Unique
    private ExtensionJarEssence.CollectorExperience pfaa$collectorExp;
    @Unique
    private final List<BlockPos> pfaa$areaSoul = new ArrayList<>(5 * 5 * 5);
    @Unique
    private final RandomGenerator pfaa$random = RandomGeneratorFactory.getDefault().create(114514_1919810L);

    public MixinJarEssence(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.pfaa$listener = new ExtensionJarEssence.ListenerJar((EssenceUtremJarBlockEntity) (Object) this);
    }

    @Override
    public ExtensionJarEssence.ListenerJar getListener() {
        return this.pfaa$listener;
    }

    @Override
    public void pfaa$tickServer(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this.pfaa$collectExp(level, pos)) return;
        this.pfaa$collectSoul(level, pos);
    }

    @Override
    public void pfaa$tickClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        this.pfaa$collectExp(level, pos);
    }

    @Unique
    private void pfaa$collectSoul(@NotNull ServerLevel level, @NotNull BlockPos pos) {
        var frequency = PFAAConfig.INSTANCE.getFrequencyEssenceCollect();
        if (frequency == 0) return;
        if (level.getGameTime() % frequency != 0) return;

        if (ExtensionJarEssence.Companion.getTypeEssence(
                (EssenceUtremJarBlockEntity) (Object) this) != EssenceType.SOULS) return;

        if (this.pfaa$areaSoul.isEmpty()) {
            BlockPos.betweenClosed(
                            pos.offset(-2, -2, -2),
                            pos.offset(2, 2, 2))
                    .forEach(posCurrent -> this.pfaa$areaSoul.add(posCurrent.mutable()));
            this.pfaa$areaSoul.remove(pos);
        }

        var posSelected = this.pfaa$areaSoul.get(this.pfaa$random.nextInt(this.pfaa$areaSoul.size()));
        var stateSelected = level.getBlockState(posSelected);
        if (!(stateSelected.is(Blocks.SOUL_SAND) || stateSelected.is(Blocks.SOUL_SOIL))) return;

        var block = PFAAConfig.INSTANCE.getSoulExtractReturns()
                ? ModBlocks.SOULLESS_SAND.get() : Blocks.AIR;
        level.setBlock(posSelected, block.defaultBlockState(), Block.UPDATE_CLIENTS);

        var value = PFAAConfig.INSTANCE.getFactorSouls();
        if (value == 0) return;
        this.addEssence(value);

        level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS
        );
    }

    @Unique
    private boolean pfaa$collectExp(@NotNull Level level, @NotNull BlockPos pos) {
        if (ExtensionJarEssence.Companion
                .getTypeEssence(ExtensionStdKt.cast(this)) !=
                EssenceType.EXPERIENCE) return false;

        if (this.pfaa$collectorExp == null)
            this.pfaa$collectorExp = new CollectorExperience(orb -> {
                var factor = PFAAConfig.INSTANCE.getFactorExperience();
                var orbValue = orb.getValue() * factor;
                if (orbValue == 0) return;

                var overflow = this.amount + orbValue - this.limit;
                this.addEssence(orbValue);

                if (overflow > 0)
                    orb.value -= (overflow / factor);
                else orb.discard();
            });

        if (this.pfaa$collectorExp.tick(level, pos)) level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS
        );
        return true;
    }
}
