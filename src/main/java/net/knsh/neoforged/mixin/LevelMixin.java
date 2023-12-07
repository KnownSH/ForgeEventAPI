package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.knsh.neoforged.accessors.ForgeLevel;
import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Level.class)
public abstract class LevelMixin implements ForgeLevel {
    @Shadow @Final public boolean isClientSide;
    @Shadow @Final private ResourceKey<Level> dimension;
    @Unique public boolean restoringBlockSnapshots = false;
    @Unique public boolean captureBlockSnapshots = false;
    @Unique public ArrayList<BlockSnapshot> capturedBlockSnapshots = new ArrayList<>();

    @Inject(
            method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;Z)Lnet/minecraft/world/level/Explosion;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V"),
            cancellable = true
    )
    private void neoforged$explode$eventCall(Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles, CallbackInfoReturnable<Explosion> cir, @Local Explosion explosion) {
        if (EventHooks.onExplosionStart((Level) (Object) this, explosion)) {
            cir.setReturnValue(explosion);
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private void neoforged$setLevelBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir, @Share("blockSnapshot") LocalRef<BlockSnapshot> blockSnapshotShare) {
        BlockSnapshot blockSnapshot = null;
        if (this.captureBlockSnapshots && !this.isClientSide) {
            blockSnapshot = BlockSnapshot.create(this.dimension, (Level) (Object) this, pos, flags);
            blockSnapshotShare.set(blockSnapshot);
            this.capturedBlockSnapshots.add(blockSnapshot);
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "RETURN", ordinal = 2)
    )
    private void neoforged$setLevelBlockReturn(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir, @Share("blockSnapshot") LocalRef<BlockSnapshot> blockSnapshotShare) {
        if (blockSnapshotShare.get() != null) {
            this.capturedBlockSnapshots.remove(blockSnapshotShare.get());
        }
    }

    @Override
    public void setCaptureBlockSnapshots(boolean bool) {
        this.captureBlockSnapshots = bool;
    }

    @Override
    public void setRestoringBlockSnapshots(boolean bool) {
        this.restoringBlockSnapshots = bool;
    }

    @Override
    public boolean getCaptureBlockSnapshots() {
        return this.captureBlockSnapshots;
    }

    @Override
    public boolean getRestoringBlockSnapshots() {
        return this.restoringBlockSnapshots;
    }

    @Override
    public ArrayList<BlockSnapshot> getCapturedBlockSnapshots() {
        return this.capturedBlockSnapshots;
    }
}
