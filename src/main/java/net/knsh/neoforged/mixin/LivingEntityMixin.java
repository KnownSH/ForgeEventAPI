package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.knsh.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique private final LivingEntity entity = (LivingEntity) (Object) this;

    @ModifyVariable(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"
            ),
            ordinal = 1)
    private float forgeevents$onLivingDamageEvent(float DamageAmount, @Local(ordinal = 0) DamageSource damageSource) {
        LivingDamageEvent livingDamageEvent = CommonHooks.onLivingDamage(entity, damageSource, DamageAmount);
        if (!livingDamageEvent.isCanceled()) {
            return livingDamageEvent.getAmount();
        }
        return 0f;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void forgeevents$onLivingDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath(entity, damageSource)) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void forgeevents$onLivingTickEvent(CallbackInfo ci) {
        if (CommonHooks.onLivingTick(entity)) ci.cancel();
    }
}
