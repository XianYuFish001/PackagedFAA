package com.fish.packaged_faa.mixin.core;

import com.fish.packaged_faa.PFAAConfig;
import com.fish.packaged_faa.mixin.extension.ExtensionJarEssence;
import com.stal111.forbidden_arcanus.common.block.entity.EssenceUtremJarBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import com.stal111.forbidden_arcanus.core.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.AABB;
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

    @Unique
    private ExtensionJarEssence.ListenerJar pfaa$listener;
    @Unique
    private AABB pfaa$areaExperience;
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
        if (this.pfaa$collectExp(level, pos, state)) return;
        this.pfaa$collectSoul(level, pos, state);
    }

    @Unique
    private void pfaa$collectSoul(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (ExtensionJarEssence.Companion.getTypeEssence(
                (EssenceUtremJarBlockEntity)(Object) this) != EssenceType.SOULS) return;
        if (level.getGameTime() % 10 != 0) return;

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

        var block = PFAAConfig.soulExtractReturns.getAsBoolean()
                ? ModBlocks.SOULLESS_SAND.get() : Blocks.AIR;
        level.setBlock(posSelected, block.defaultBlockState(), Block.UPDATE_CLIENTS);

        this.addEssence(4);
        assert this.level != null;
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS
        );
    }

    @Unique
    private boolean pfaa$collectExp(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (ExtensionJarEssence.Companion.getTypeEssence(
                (EssenceUtremJarBlockEntity)(Object) this) != EssenceType.EXPERIENCE) return false;
        if (level.getGameTime() % 10 != 0) return false;

        if (this.pfaa$areaExperience == null) {
            var posStart = pos.offset(-2, -2, -2);
            var posEnd = pos.offset(2, 2, 2);
            this.pfaa$areaExperience = new AABB(
                    posStart.getX(), posStart.getY(), posStart.getZ(),
                    posEnd.getX(), posEnd.getY(), posEnd.getZ()
            );
        }

        var experiences =
                level.getEntitiesOfClass(ExperienceOrb.class, this.pfaa$areaExperience);

        for (ExperienceOrb experienceOrb : experiences) {
            int orbValue = experienceOrb.getValue() * 2;
            this.addEssence(orbValue);
            experienceOrb.discard();
        }
        assert this.level != null;
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS
        );
        return true;
    }
}
