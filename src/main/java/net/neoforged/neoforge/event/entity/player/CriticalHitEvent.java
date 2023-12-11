package net.neoforged.neoforge.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public class CriticalHitEvent extends PlayerEvent {
    public static final Event<ICriticalHitEvent> EVENT = ForgeEventFactory.create(ICriticalHitEvent.class, (listeners) -> (event) -> {
        for (ICriticalHitEvent listener : listeners) {
            event = listener.onEvent(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface ICriticalHitEvent {
        CriticalHitEvent onEvent(CriticalHitEvent event);
    }

    private float damageModifier;
    private final float oldDamageModifier;
    private final Entity target;
    private final boolean vanillaCritical;

    public CriticalHitEvent(Player player, Entity target, float damageModifier, boolean vanillaCritical) {
        super(player);
        this.target = target;
        this.damageModifier = damageModifier;
        this.oldDamageModifier = damageModifier;
        this.vanillaCritical = vanillaCritical;
    }

    /**
     * The Entity that was damaged by the player.
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * This set the damage multiplier for the hit.
     * If you set it to 0, then the particles are still generated but damage is not done.
     */
    public void setDamageModifier(float mod) {
        this.damageModifier = mod;
    }

    /**
     * The damage modifier for the hit.<br>
     * This is by default 1.5F for ciritcal hits and 1F for normal hits .
     */
    public float getDamageModifier() {
        return this.damageModifier;
    }

    /**
     * The orignal damage modifier for the hit wthout any changes.<br>
     * This is 1.5F for ciritcal hits and 1F for normal hits .
     */
    public float getOldDamageModifier() {
        return this.oldDamageModifier;
    }

    /**
     * Returns true if this hit was critical by vanilla
     */
    public boolean isVanillaCritical() {
        return vanillaCritical;
    }
}
