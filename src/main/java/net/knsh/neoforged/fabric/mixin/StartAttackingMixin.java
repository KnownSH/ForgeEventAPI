package net.knsh.neoforged.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.knsh.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(StartAttacking.class)
public class StartAttackingMixin {
    @Inject(
            method = "method_47123",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"))
    private static void forgeevents$onAttackEvent(Predicate predicate, Function function, MemoryAccessor<com.mojang.datafixers.kinds.K1, LivingEntity> memoryAccessor, MemoryAccessor memoryAccessor2, ServerLevel world, Mob mob, long l, CallbackInfoReturnable<Boolean> cir, @Local LocalRef<LivingEntity> livingEntity) {
        LivingChangeTargetEvent changeTargetEvent = CommonHooks.onLivingChangeTarget(mob, livingEntity.get(), LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if (!changeTargetEvent.isCanceled()) {
            memoryAccessor.set(changeTargetEvent.getNewTarget());
        }
    }
}
