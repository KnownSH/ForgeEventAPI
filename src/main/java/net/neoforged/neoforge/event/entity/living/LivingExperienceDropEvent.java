package net.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class LivingExperienceDropEvent extends LivingEvent implements ICancellableEvent {
    public static final Event<LivingExperienceDrop> EVENT = ForgeEventFactory.create(LivingExperienceDrop.class, (listeners) -> (event) -> {
        for (LivingExperienceDrop listener : listeners) {
            listener.onLivingExperienceDrop(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface LivingExperienceDrop {
        LivingExperienceDropEvent onLivingExperienceDrop(LivingExperienceDropEvent event);
    }

    @Nullable
    private final Player attackingPlayer;
    private final int originalExperiencePoints;

    private int droppedExperiencePoints;

    public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
        super(entity);

        this.attackingPlayer = attackingPlayer;
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    public int getDroppedExperience() {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperiencePoints = droppedExperience;
    }

    /**
     * @return The player that last attacked the entity and thus caused the experience. This can be null, in case the player has since logged out.
     */
    @Nullable
    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    public int getOriginalExperience() {
        return originalExperiencePoints;
    }
}
