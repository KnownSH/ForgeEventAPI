package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Method;
import java.util.List;

public abstract class CustomizeGuiOverlayEvent extends ForgeEvent {
    private final Window window;
    private final GuiGraphics guiGraphics;
    private final float partialTick;

    @ApiStatus.Internal
    protected CustomizeGuiOverlayEvent(Window window, GuiGraphics guiGraphics, float partialTick) {
        this.window = window;
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
    }

    public Window getWindow() {
        return window;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public float getPartialTick() {
        return partialTick;
    }

    /**
     * Fired <b>before</b> a boss health bar is rendered to the screen.
     *
     * <p>This event is {@linkplain net.neoforged.bus.api.ICancellableEvent cancellable}, and does not {HasResult have a result}.
     * Cancelling this event will prevent the given bar from rendering.</p>
     *
     * <p>This event is fired on the {@linkplain net.neoforged.neoforge.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#CLIENT logical client}.</p>
     */
    public static class BossEventProgress extends CustomizeGuiOverlayEvent implements ICancellableEvent {
        public static final Event<IBossEventProgress> EVENT = ForgeEventFactory.create(IBossEventProgress.class, (listeners) -> (event) -> {
            for (IBossEventProgress listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IBossEventProgress {
            BossEventProgress onEvent(BossEventProgress event);
        }

        private final LerpingBossEvent bossEvent;
        private final int x;
        private final int y;
        private int increment;

        @ApiStatus.Internal
        public BossEventProgress(Window window, GuiGraphics guiGraphics, float partialTick, LerpingBossEvent bossEvent, int x, int y, int increment) {
            super(window, guiGraphics, partialTick);
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        /**
         * @return the boss health bar currently being rendered
         */
        public LerpingBossEvent getBossEvent() {
            return bossEvent;
        }

        /**
         * {@return the X position of the boss health bar}
         */
        public int getX() {
            return x;
        }

        /**
         * {@return the Y position of the boss health bar}
         */
        public int getY() {
            return y;
        }

        /**
         * {@return the Y position increment before rendering the next boss health bar}
         */
        public int getIncrement() {
            return increment;
        }

        /**
         * Sets the Y position increment before rendering the next boss health bar.
         *
         * @param increment the new Y position increment
         */
        public void setIncrement(int increment) {
            this.increment = increment;
        }
    }

    /**
     * Fired <b>before</b> textual information is rendered to the debug screen.
     * This can be used to add or remove text information.
     *
     * <p>This event is not {@linkplain ICancellableEvent cancellable}, and does not {HasResult have a result}.</p>
     *
     * <p>This event is fired on the {@linkplain net.neoforged.neoforge.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#CLIENT logical client}.</p>
     */
    public static class DebugText extends CustomizeGuiOverlayEvent {
        public static final Event<IDebugText> EVENT = ForgeEventFactory.create(IDebugText.class, (listeners) -> (event) -> {
            for (IDebugText listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IDebugText {
            DebugText onEvent(DebugText event);
        }

        private final List<String> left;
        private final List<String> right;

        @ApiStatus.Internal
        public DebugText(Window window, GuiGraphics guiGraphics, float partialTick, List<String> left, List<String> right) {
            super(window, guiGraphics, partialTick);
            this.left = left;
            this.right = right;
        }

        /**
         * @return the modifiable list of text to render on the left side
         */
        public List<String> getLeft() {
            return left;
        }

        /**
         * @return the modifiable list of text to render on the right side
         */
        public List<String> getRight() {
            return right;
        }
    }

    /**
     * Fired <b>before</b> the chat messages overlay is rendered to the screen.
     *
     * <p>This event is not {@linkplain ICancellableEvent cancellable}, and does not {HasResult have a result}.<p/>
     *
     * <p>This event is fired on the {@linkplain net.neoforged.neoforge.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#CLIENT logical client}.</p>
     */
    public static class Chat extends CustomizeGuiOverlayEvent {
        public static final Event<IChat> EVENT = ForgeEventFactory.create(IChat.class, (listeners) -> (event) -> {
            for (IChat listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IChat {
            Chat onEvent(Chat event);
        }

        private int posX;
        private int posY;

        @ApiStatus.Internal
        public Chat(Window window, GuiGraphics guiGraphics, float partialTick, int posX, int posY) {
            super(window, guiGraphics, partialTick);
            this.setPosX(posX);
            this.setPosY(posY);
        }

        /**
         * @return the X position of the chat messages overlay
         */
        public int getPosX() {
            return posX;
        }

        /**
         * Sets the new X position for rendering the chat messages overlay
         *
         * @param posX the new X position
         */
        public void setPosX(int posX) {
            this.posX = posX;
        }

        /**
         * @return the Y position of the chat messages overlay
         */
        public int getPosY() {
            return posY;
        }

        /**
         * Sets the new Y position for rendering the chat messages overlay
         *
         * @param posY the new y position
         */
        public void setPosY(int posY) {
            this.posY = posY;
        }
    }
}
