package net.knsh.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

public class LivingDamageEvent extends ForgeEvent implements ICancellableEvent {
    public static final Event<LivingDamage> EVENT = ForgeEventFactory.create(LivingDamage.class, (listeners) -> (event) -> {
        for (LivingDamage listener : listeners) {
            listener.onLivingDamage(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface LivingDamage {
        LivingDamageEvent onLivingDamage(LivingDamageEvent event);
    }

    private final DamageSource source;
    private float amount;
    private LivingEntity entity;

    public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
