package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.knsh.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
    @WrapOperation(
            method = "serverTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z")
    )
    private boolean neoforged$serverTick1(Mob instance, LevelReader level, Operation<Boolean> original, ServerLevel serverLevel, @Local Mob mob, @Local SpawnData spawnData) {
        return EventHooks.checkSpawnPositionSpawner(mob, serverLevel, MobSpawnType.SPAWNER, spawnData, (BaseSpawner) (Object) this);
    }

    @ModifyExpressionValue(
            method = "serverTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z")
    )
    private boolean neoforged$serverTick2(boolean original, ServerLevel serverLevel, @Local Entity entity, @Local CompoundTag compoundTag, @Share("eventData")LocalRef<MobSpawnEvent.FinalizeSpawn> eventData) {
        MobSpawnEvent.FinalizeSpawn event = EventHooks.onFinalizeSpawnSpawner((Mob) entity, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), null, compoundTag, (BaseSpawner) (Object) this);
        eventData.set(event);
        return original && event != null;
    }

    @ModifyArgs(
            method = "serverTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;")
    )
    private void neoforged$finalizeSpawn(Args args, @Share("eventData")LocalRef<MobSpawnEvent.FinalizeSpawn> eventData) {
        args.set(1, eventData.get().getDifficulty());
        args.set(2, eventData.get().getSpawnType());
        args.set(3, eventData.get().getSpawnData());
        args.set(4, eventData.get().getSpawnTag());
    }
}
