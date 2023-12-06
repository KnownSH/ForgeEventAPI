package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public class EnderDragonMixin extends Mob {
    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Unique @Nullable private Player unlimitedLastHurtByPlayer;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void forgeevents$setLastHurtByPlayer(CallbackInfo ci) {
        // lastHurtByPlayer is cleared after 100 ticks, capture it indefinitely in unlimitedLastHurtByPlayer for LivingExperienceDropEvent
        if (this.lastHurtByPlayer != null) this.unlimitedLastHurtByPlayer = lastHurtByPlayer;
        if (this.unlimitedLastHurtByPlayer != null && this.unlimitedLastHurtByPlayer.isRemoved()) this.unlimitedLastHurtByPlayer = null;
    }

    @WrapOperation(
            method = "tickDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V")
    )
    private void forgeevents$getExperienceDropEvent(ServerLevel level, Vec3 pos, int amount, Operation<Void> original, @Local int i) {
        int award = EventHooks.getExperienceDrop((EnderDragon) (Object) this, this.unlimitedLastHurtByPlayer, Mth.floor((float) i * 0.08F));
        original.call(level, pos, award);
    }

    @WrapOperation(
            method = "tickDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V", ordinal = 1)
    )
    private void forgeevents$getExperienceDropEvent2(ServerLevel level, Vec3 pos, int amount, Operation<Void> original, @Local int i) {
        int award = EventHooks.getExperienceDrop((EnderDragon) (Object) this, this.unlimitedLastHurtByPlayer, Mth.floor((float) i * 0.2F));
        original.call(level, pos, award);
    }
}
