package net.knsh.neoforged.mixin.server.level;

import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$onPlayerDeathEvent(DamageSource source, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath((ServerPlayer) (Object) this, source)) {
            ci.cancel();
        }
    }
}
