package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.model.EmissiveSkinFeatureRenderer;
import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import dev.proststuff.skinmatics.client.model.EyeOverlayFeatureRenderer;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class SkinmaticsClientEvents {
    private static boolean playerWorldLoaded = false;

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(SkinmaticsClientEvents::clientInitialized);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerPlayerFeatures);
        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsClientEvents::clientTicked);
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(Skinmatics.of("skinmatics_textures"), SkinmaticsClientEvents::onResourceReload);
    }

    private static CompletableFuture<Void> onResourceReload(ResourceReloader.Store store, Executor prepareExecutor, ResourceReloader.Synchronizer reloadSynchronizer, Executor applyExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose(reloadSynchronizer::whenPrepared).thenRunAsync(() -> SkinmaticsClient.TEXTURE_MANAGER.reload(), applyExecutor);
    }

    private static void clientInitialized(MinecraftClient client) {
        SkinmaticsClient.refreshConfig();
        SkinmaticsClient.TEXTURE_MANAGER = new SkinmaticsTextureManager();
        SkinmaticsClient.LOCAL_UUID = client.getGameProfile().id();
    }

    @SuppressWarnings("unchecked")
    private static void registerPlayerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {
        if (livingEntityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
            registrationHelper.register(new EyeFeatureRenderer(playerEntityRenderer, EyeFeatureRenderer.EyePosition.RIGHT));
            registrationHelper.register(new EyeFeatureRenderer(playerEntityRenderer, EyeFeatureRenderer.EyePosition.LEFT));

            registrationHelper.register(new EyeOverlayFeatureRenderer(playerEntityRenderer, EyeFeatureRenderer.EyePosition.RIGHT));
            registrationHelper.register(new EyeOverlayFeatureRenderer(playerEntityRenderer, EyeFeatureRenderer.EyePosition.LEFT));

            registrationHelper.register(new EmissiveSkinFeatureRenderer(playerEntityRenderer));
        }
    }

    private static void clientTicked(MinecraftClient client) {
        ClientWorld world = client.world;
        ClientPlayerEntity clientPlayer = client.player;

        if (world != null && !playerWorldLoaded) {
            playerWorldLoaded = true;
            SkinmaticsClient.refreshLocalSkinmatics();
        } else if (world == null && playerWorldLoaded) {
            playerWorldLoaded = false;
        }

        if (world == null || clientPlayer == null) return;
        if (world.getTickManager().isFrozen() || client.isPaused()) return;

        BlockPos pos1 = clientPlayer.getBlockPos();
        int updateRange = SkinmaticsClient.CONFIG.playerUpdateRange;
        Map<UUID, SkinmaticsData> skinmatics = SkinmaticsClient.SKINMATICS;

        for (AbstractClientPlayerEntity player : world.getPlayers()) {
            SkinmaticsData data = skinmatics.get(player.getUuid());
            BlockPos pos2 = player.getBlockPos();
            boolean isLocalPlayer = player.isMainPlayer();

            if (data == null) continue;
            if (!isLocalPlayer && pos2.getSquaredDistance(pos1) > (updateRange * updateRange)) continue;
            if (player.isInvisible()) continue;

            updateSkin(data);
            updateEyes(player, data, player.getRandom());
        }
    }

    private static void updateSkin(SkinmaticsData data) {
       data.tick();
    }

    private static void updateEyes(AbstractClientPlayerEntity player, SkinmaticsData data, Random random) {
        float yaw = MathHelper.wrapDegrees(player.getHeadYaw() - player.bodyYaw);
        float pitch = player.getPitch();

        EyeFeatureRenderer.EyeDirection eyeDirectionState = EyeFeatureRenderer.EyeDirection.FRONT;

        if (data.blinkingTicks <= 0) {
            boolean up = pitch < -20;
            boolean down = pitch > 20;
            boolean right = yaw > 20;
            boolean left = yaw < -20;

            if (up && right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT_UP;
            else if (up && left) eyeDirectionState = EyeFeatureRenderer.EyeDirection.LEFT_UP;
            else if (down && right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT_DOWN;
            else if (down && left) eyeDirectionState = EyeFeatureRenderer.EyeDirection.LEFT_DOWN;
            else if (up) eyeDirectionState =  EyeFeatureRenderer.EyeDirection.UP;
            else if (down) eyeDirectionState =   EyeFeatureRenderer.EyeDirection.DOWN;
            else if (right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT;
            else if (left) eyeDirectionState =  EyeFeatureRenderer.EyeDirection.LEFT;

            if (random.nextInt(data.blinkingChance) == 0) {
                data.blinkingTicks = data.holdBlinkingFor;
            }
        } else {
            eyeDirectionState = EyeFeatureRenderer.EyeDirection.CLOSED;
        }

        int state = eyeDirectionState.ordinal();
        data.getFaceLayerStateOf(EyeFeatureRenderer.EyePosition.RIGHT, false).state = state;
        data.getFaceLayerStateOf(EyeFeatureRenderer.EyePosition.RIGHT, true).state = state;
        data.getFaceLayerStateOf(EyeFeatureRenderer.EyePosition.LEFT, false).state = state;
        data.getFaceLayerStateOf(EyeFeatureRenderer.EyePosition.LEFT, true).state = state;
    }
}
