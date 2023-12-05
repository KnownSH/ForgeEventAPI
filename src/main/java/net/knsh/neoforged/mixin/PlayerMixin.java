package net.knsh.neoforged.mixin;

import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void forgeevents$onPlayerDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath((Player) (Object) this, damageSource)) {
            ci.cancel();
        }
    }
}
