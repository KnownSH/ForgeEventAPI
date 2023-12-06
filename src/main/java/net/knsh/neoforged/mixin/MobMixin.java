package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.knsh.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
    @WrapOperation(
            method = "setTarget",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;", opcode = Opcodes.PUTFIELD)
    )
    private void forgeevents$onSetTarget(Mob instance, LivingEntity value, Operation<LivingEntity> original) {
        LivingChangeTargetEvent changeTargetEvent = CommonHooks.onLivingChangeTarget(instance, value, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        if (!changeTargetEvent.isCanceled()) {
            original.call(instance, changeTargetEvent.getNewTarget());
        }
    }
}
