package net.knsh.forgeeventapiport.mixin.world.entity.animal.horse;

import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
    @Inject(
            method = "executeRidersJump",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;hasImpulse:Z")
    )
    private void neoforged$executeRidersJumpEventCall(float playerJumpPendingScale, Vec3 travelVector, CallbackInfo ci) {
        CommonHooks.onLivingJump((AbstractHorse) (Object) this);
    }
}
