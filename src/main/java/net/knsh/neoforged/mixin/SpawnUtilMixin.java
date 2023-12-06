package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SpawnUtil.class)
public class SpawnUtilMixin {
    @Inject(
            method = "trySpawnMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z"),
            cancellable = true
    )
    private static <T extends Mob> void neoforged$onTrySpawnMob(EntityType<T> entityType, MobSpawnType spawnType, ServerLevel level, BlockPos pos, int attempts, int i, int yOffset, SpawnUtil.Strategy strategy, CallbackInfoReturnable<Optional<T>> cir, @Local T mob) {
        if (EventHooks.checkSpawnPosition(mob, level, spawnType)) {
            level.addFreshEntity(mob);
            cir.setReturnValue(Optional.of(mob));
        }
    }
}
