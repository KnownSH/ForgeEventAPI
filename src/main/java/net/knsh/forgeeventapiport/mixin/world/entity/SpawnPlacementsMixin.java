package net.knsh.forgeeventapiport.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnPlacements.class)
public class SpawnPlacementsMixin {
    @ModifyReturnValue(
            method = "checkSpawnRules",
            at = @At("RETURN")
    )
    private static <T extends Entity> boolean neoforged$checkSpawnRules(
            boolean original, EntityType<T> entityType, ServerLevelAccessor serverLevel, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        return EventHooks.checkSpawnPlacements(entityType, serverLevel, spawnType, pos, random, original);
    }
}
