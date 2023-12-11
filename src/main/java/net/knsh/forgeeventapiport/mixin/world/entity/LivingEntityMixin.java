package net.knsh.forgeeventapiport.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow @Nullable protected Player lastHurtByPlayer;

    @Shadow public abstract int getExperienceReward();

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Unique private final LivingEntity entity = (LivingEntity) (Object) this;

    // MobEffectEvent.Remove event
    @Inject(
            method = "removeAllEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
    )
    private void neoforged$removeAllEffects$eventCall(CallbackInfoReturnable<Boolean> cir, @Local Iterator<MobEffectInstance> iterator, @Share("mobEffectRemoveCancelled") LocalBooleanRef isCancelled) {
        MobEffectInstance effect = iterator.next();
        if (MobEffectEvent.Remove.EVENT.invoker().onRemoveEvent(new MobEffectEvent.Remove(entity, effect)).isCanceled()) {
            isCancelled.set(true);
        }
    }

    @WrapWithCondition(
            method = "removeAllEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
    )
    private boolean neoforged$removeAllEffects$condition1(LivingEntity instance, MobEffectInstance effectInstance, @Share("mobEffectRemoveCancelled") LocalBooleanRef isCancelled) {
        return !isCancelled.get();
    }

    @WrapWithCondition(
            method = "removeAllEffects",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V")
    )
    private boolean neoforged$removeAllEffects$condition2(Iterator<MobEffectInstance> instance, @Share("mobEffectRemoveCancelled") LocalBooleanRef isCancelled) {
        return !isCancelled.get();
    }

    @Inject(
            method = "removeEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$removeEffect$eventCall(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (MobEffectEvent.Remove.EVENT.invoker().onRemoveEvent(new MobEffectEvent.Remove(entity, effect)).isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    // MobEffectEvent.Applicable event
    @Inject(
            method = "canBeAffected",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$canBeAffected$eventCall(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(entity, effectInstance);
        event = MobEffectEvent.Applicable.EVENT.invoker().onApplicableEvent(event);
        if (event.getResult() != ForgeEvent.Result.DEFAULT) cir.setReturnValue(event.getResult() == ForgeEvent.Result.ALLOW);
    }

    // MobEffectEvent.Added event
    @Inject(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/effect/MobEffectInstance;getEffect()Lnet/minecraft/world/effect/MobEffect;")
    )
    private void neoforged$addEffect$eventCall(MobEffectInstance effectInstance, Entity ent, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) MobEffectInstance mobEffectInstance) {
        MobEffectEvent.Added.EVENT.invoker().onAddedEvent(new MobEffectEvent.Added(entity, mobEffectInstance, effectInstance, ent));
    }

    // MobEffectEvent.Expired event
    @ModifyExpressionValue(
            method = "tickEffects",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z")
    )
    private boolean neoforged$tickEffects$eventCall(boolean original, @Local MobEffectInstance mobEffectInstance) {
        return !(!original && !MobEffectEvent.Expired.EVENT.invoker().onExpiredEvent(new MobEffectEvent.Expired(entity, mobEffectInstance)).isCanceled());
    }

    // Living Jump Event
    @Inject(
            method = "jumpFromGround",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hasImpulse:Z")
    )
    private void neoforged$onLivingJumpEvent(CallbackInfo ci) {
        CommonHooks.onLivingJump(entity);
    }


    // living visibility event
    @ModifyReturnValue(
            method = "getVisibilityPercent",
            at = @At(value = "RETURN")
    )
    private double neoforged$onLivingVisibilityEvent(double original, @Nullable Entity lookingEntity) {
        return CommonHooks.getEntityVisibilityMultiplier(entity, lookingEntity, original);
    }


    // Living Damage Event
    @ModifyVariable(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"
            ),
            ordinal = 1)
    private float neoforged$onLivingDamageEvent(float DamageAmount, @Local(ordinal = 0) DamageSource damageSource) {
        LivingDamageEvent livingDamageEvent = CommonHooks.onLivingDamage(entity, damageSource, DamageAmount);
        if (!livingDamageEvent.isCanceled()) {
            return livingDamageEvent.getAmount();
        }
        return 0f;
    }

    @WrapOperation(
            method = "dropExperience",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V")
    )
    private void neoforged$onLivingExperienceDropEvent(ServerLevel level, Vec3 pos, int amount, Operation<Void> original) {
        int reward = EventHooks.getExperienceDrop((LivingEntity) (Object) this, this.lastHurtByPlayer, this.getExperienceReward());
        original.call(level, pos, reward);
    }

    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$onLivingDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath(entity, damageSource)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$onLivingTickEvent(CallbackInfo ci) {
        if (CommonHooks.onLivingTick(entity)) ci.cancel();
    }
}
