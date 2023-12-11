package net.knsh.neoforged.mixin.world.entity.monster;

import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.entity.monster.MagmaCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaCube.class)
public class MagmaCubeMixin {
    @Inject(
            method = "jumpFromGround",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/MagmaCube;hasImpulse:Z")
    )
    private void neoforged$onMagmaCubeJumpEvent(CallbackInfo ci) {
        CommonHooks.onLivingJump((MagmaCube) (Object) this);
    }
}
