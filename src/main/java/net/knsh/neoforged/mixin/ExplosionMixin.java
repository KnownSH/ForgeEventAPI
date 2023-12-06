package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private Level level;

    @Shadow @Final private float radius;

    @Inject(
            method = "explode",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;")
    )
    private void neoforged$explode$eventCall(CallbackInfo ci, @Local List<Entity> list) {
        EventHooks.onExplosionDetonate(this.level, (Explosion) (Object) this, list, this.radius * 2.0F);
    }
}
