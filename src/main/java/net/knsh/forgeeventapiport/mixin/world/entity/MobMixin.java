package net.knsh.forgeeventapiport.mixin.world.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.knsh.forgeeventapiport.accessors.ForgeEntity;
import net.knsh.forgeeventapiport.accessors.ForgeMob;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements ForgeMob {
    @Unique private boolean spawnCancelled = false;

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    public final boolean isSpawnCancelled() {
        return this.spawnCancelled;
    }

    @Unique
    public final void setSpawnCancelled(boolean cancel) {
        if (((ForgeEntity) this).isAddedToWorld()) {
            throw new UnsupportedOperationException("Late invocations of Mob#setSpawnCancelled are not supported!");
        }
        this.spawnCancelled = cancel;
    }

    @WrapOperation(
            method = "setTarget",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;", opcode = Opcodes.PUTFIELD)
    )
    private void neoforged$onSetTarget(Mob instance, LivingEntity value, Operation<LivingEntity> original) {
        LivingChangeTargetEvent changeTargetEvent = CommonHooks.onLivingChangeTarget(instance, value, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if (!changeTargetEvent.isCanceled()) {
            original.call(instance, changeTargetEvent.getNewTarget());
        }
    }

    @Inject(
            method = "checkDespawn",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;")
    )
    private void neoforged$onCheckDespawn(CallbackInfo ci, @Local LocalRef<Entity> entity) {
        ForgeEvent.Result result = EventHooks.canEntityDespawn((Mob) (Object) this, (ServerLevel) this.level());
        if (result == ForgeEvent.Result.DENY) {
            noActionTime = 0;
            entity.set(null);
        } else if (result == ForgeEvent.Result.ALLOW) {
            this.discard();
            entity.set(null);
        }
    }
}
