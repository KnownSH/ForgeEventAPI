package net.knsh.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Fires at various times during LevelRenderer.renderLevel.
 * Check {@link #getStage} to render during the appropriate time for your use case.
 *
 * <p>This event is not {@linkplain net.knsh.neoforged.bus.api.ICancellableEvent cancellable}, and does not {HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain net.knsh.neoforged.NeoForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain EnvType#CLIENT logical client}. </p>
 */
@Environment(EnvType.CLIENT)
public class RenderLevelStageEvent extends ForgeEvent {
    public static final Event<IRenderLevelStageEvent> EVENT = ForgeEventFactory.create(IRenderLevelStageEvent.class, (listeners) -> (event) -> {
        for (IRenderLevelStageEvent listener : listeners) {
            event = listener.onRenderLevelStageEvent(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface IRenderLevelStageEvent {
        RenderLevelStageEvent onRenderLevelStageEvent(RenderLevelStageEvent event);
    }

    private final Stage stage;
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final float partialTick;
    private final Camera camera;
    private final Frustum frustum;

    public RenderLevelStageEvent(Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTick, Camera camera, Frustum frustum) {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
        this.camera = camera;
        this.frustum = frustum;
    }

    /**
     * {@return the current {@linkplain Stage stage} that is being rendered. Check this before doing rendering to ensure
     * that rendering happens at the appropriate time.}
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * {@return the level renderer}
     */
    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack() {
        return poseStack;
    }

    /**
     * {@return the projection matrix}
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * {@return the current "ticks" value in the {@linkplain LevelRenderer level renderer}}
     */
    public int getRenderTick() {
        return renderTick;
    }

    /**
     * {@return the current partialTick value used for rendering}
     */
    public float getPartialTick() {
        return partialTick;
    }

    /**
     * {@return the camera}
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * {@return the frustum}
     */
    public Frustum getFrustum() {
        return frustum;
    }

    /**
     * A time during level rendering for you to render custom things into the world.
     *
     * RegisterStageEvent
     */
    public static class Stage {
        private static final Map<RenderType, Stage> RENDER_TYPE_STAGES = new HashMap<>();

        /**
         * Use this to render custom objects into the skybox.
         * Called regardless of if they sky actually renders or not.
         */
        public static final Stage AFTER_SKY = register("after_sky", null);
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_SOLID_BLOCKS = register("after_solid_blocks", RenderType.solid());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS = register("after_cutout_mipped_blocks", RenderType.cutoutMipped());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_CUTOUT_BLOCKS = register("after_cutout_blocks", RenderType.cutout());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_ENTITIES = register("after_entities", null);
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_BLOCK_ENTITIES = register("after_block_entities", null);
        /**
         * Use this to render custom block-like geometry into the world.
         * Due to how transparency sorting works, this stage may not work properly with translucency. If you intend to render translucency,
         * try using {@link #AFTER_TRIPWIRE_BLOCKS} or {@link #AFTER_PARTICLES}.
         * Although this is called within a fabulous graphics target, it does not function properly in many cases.
         */
        public static final Stage AFTER_TRANSLUCENT_BLOCKS = register("after_translucent_blocks", RenderType.translucent());
        /**
         * Use this to render custom block-like geometry into the world.
         */
        public static final Stage AFTER_TRIPWIRE_BLOCKS = register("after_tripwire_blocks", RenderType.tripwire());
        /**
         * Use this to render custom effects into the world, such as custom entity-like objects or special rendering effects.
         * Called within a fabulous graphics target.
         * Happens after entities render.
         *
         * NeoForgeRenderTypes#TRANSLUCENT_ON_PARTICLES_TARGET
         */
        public static final Stage AFTER_PARTICLES = register("after_particles", null);
        /**
         * Use this to render custom weather effects into the world.
         * Called within a fabulous graphics target.
         */
        public static final Stage AFTER_WEATHER = register("after_weather", null);
        /**
         * Use this to render after everything in the level has been rendered.
         * Called after {@link LevelRenderer#renderLevel(PoseStack, float, long, boolean, Camera, GameRenderer, LightTexture, Matrix4f)} (PoseStack, float, long, boolean, Camera, GameRenderer, LightTexture, Matrix4f)} finishes.
         */
        public static final Stage AFTER_LEVEL = register("after_level", null);

        private final String name;

        private Stage(String name) {
            this.name = name;
        }

        private static Stage register(ResourceLocation name, @Nullable RenderType renderType) throws IllegalArgumentException {
            Stage stage = new Stage(name.toString());
            if (renderType != null && RENDER_TYPE_STAGES.putIfAbsent(renderType, stage) != null)
                throw new IllegalArgumentException("Attempted to replace an existing RenderLevelStageEvent.Stage for a RenderType: Stage = " + stage + ", RenderType = " + renderType);
            return stage;
        }

        private static Stage register(String name, @Nullable RenderType renderType) throws IllegalArgumentException {
            return register(new ResourceLocation(name), renderType);
        }

        @Override
        public String toString() {
            return this.name;
        }

        /**
         * {@return the {@linkplain Stage stage} bound to the {@linkplain RenderType render type}, or null if no value is present}
         */
        @Nullable
        public static Stage fromRenderType(RenderType renderType) {
            return RENDER_TYPE_STAGES.get(renderType);
        }
    }
}
