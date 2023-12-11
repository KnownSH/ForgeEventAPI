package net.knsh.forgeeventapiport.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {


    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$onPlayerDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath((Player) (Object) this, damageSource)) {
            ci.cancel();
        }
    }
    
    @Inject(
            method = "attack",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1)
    )
    private void neoforged$criticalHitEvent(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2, @Share("critEvent") LocalRef<CriticalHitEvent> critEvent) {
        CriticalHitEvent hitResult = CommonHooks.getCriticalHit((Player) (Object) this, target, flag2.get(), flag2.get() ? 1.5F : 1.0F);
        flag2.set(hitResult != null);
        critEvent.set(hitResult);
    }
    
    @ModifyConstant(
            method = "attack",
            constant = @Constant(floatValue = 1.5F)
    )
    private float neoforged$modifyCriticalHitDamage(float constant, @Share("critEvent") LocalRef<CriticalHitEvent> critEvent) {
        return critEvent.get().getDamageModifier();
    }
}
