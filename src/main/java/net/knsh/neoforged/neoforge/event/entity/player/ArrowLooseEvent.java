package net.knsh.neoforged.neoforge.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class ArrowLooseEvent extends PlayerEvent implements ICancellableEvent {
    public static final Event<IArrowLooseEvent> EVENT = ForgeEventFactory.create(IArrowLooseEvent.class, (listeners) -> (event) -> {
        for (IArrowLooseEvent listener : listeners) {
            event = listener.onEvent(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface IArrowLooseEvent {
        ArrowLooseEvent onEvent(ArrowLooseEvent event);
    }

    private final ItemStack bow;
    private final Level level;
    private final boolean hasAmmo;
    private int charge;

    public ArrowLooseEvent(Player player, @NotNull ItemStack bow, Level level, int charge, boolean hasAmmo) {
        super(player);
        this.bow = bow;
        this.level = level;
        this.charge = charge;
        this.hasAmmo = hasAmmo;
    }

    @NotNull
    public ItemStack getBow() {
        return this.bow;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean hasAmmo() {
        return this.hasAmmo;
    }

    public int getCharge() {
        return this.charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}
