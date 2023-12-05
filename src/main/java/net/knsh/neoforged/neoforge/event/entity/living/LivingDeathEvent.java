package net.knsh.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in
 * {@link LivingEntity#die(DamageSource)},
 * {@link net.minecraft.world.entity.player.Player#die(DamageSource)}, and
 * {@link net.minecraft.server.level.ServerPlayer#die(DamageSource)}. <br>
 * <br>
 * This event is fired via the {@link net.knsh.neoforged.neoforge.common.CommonHooks#onLivingDeath(LivingEntity, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link net.knsh.neoforged.bus.api.ICancellableEvent}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {HasResult}<br>
 * <br>
 * This event is fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.
 **/
public class LivingDeathEvent extends LivingEvent implements ICancellableEvent {
    public static final Event<LivingDeath> EVENT = ForgeEventFactory.create(LivingDeath.class, (listeners) -> (event) -> {
        for (LivingDeath listener : listeners) {
            listener.onLivingDeath(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface LivingDeath {
        LivingDeathEvent onLivingDeath(LivingDeathEvent event);
    }

    private final DamageSource source;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }
}
