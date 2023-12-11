package net.knsh.forgeeventapiport.mixin.world.level;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @ModifyReturnValue(
            method = "isValidPositionForMob",
            at = @At(value = "RETURN", ordinal = 1)
    )
    private static boolean neoforged$isValidPositionForMob(boolean original, ServerLevel level, Mob mob) {
        return EventHooks.checkSpawnPosition(mob, level, MobSpawnType.NATURAL);
    }

    @WrapOperation(
            method = "spawnMobsForChunkGeneration",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z")
    )
    private static boolean neoforged$checkSpawnRules(Mob instance, LevelAccessor level, MobSpawnType reason, Operation<Boolean> original, ServerLevelAccessor levelAccessor, @Share("checkSpawnPosition") LocalBooleanRef complete) {
        boolean result = EventHooks.checkSpawnPosition(instance, levelAccessor, reason);
        complete.set(result);
        return result;
    }

    @WrapOperation(
            method = "spawnMobsForChunkGeneration",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z")
    )
    private static boolean neoforged$checkSpawnObstruction(Mob instance, LevelReader level, Operation<Boolean> original, @Share("checkSpawnPosition") LocalBooleanRef complete) {
        return complete.get();
    }
}
