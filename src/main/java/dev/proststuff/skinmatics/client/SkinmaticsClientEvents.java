package dev.proststuff.skinmatics.client;

import com.mojang.brigadier.CommandDispatcher;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.renderer.PaperDollRenderer;
import dev.proststuff.skinmatics.client.renderer.layer.EmissiveSkinLayer;
import dev.proststuff.skinmatics.client.renderer.layer.EyesLayer;
import dev.proststuff.skinmatics.client.skinmatics.ProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.TrackedProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsTextureManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkinmaticsClientEvents {
    public static void init() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(SkinmaticsMod.of("skinmatics_textures"), SkinmaticsClientEvents::onResourceReload);
        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsClientEvents::tickClient);

        ClientPlayConnectionEvents.JOIN.register(SkinmaticsClientEvents::connected);
        ClientPlayConnectionEvents.DISCONNECT.register(SkinmaticsClientEvents::disconnected);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerPlayerFeatures);
        ClientTickEvents.END_LEVEL_TICK.register(SkinmaticsClientEvents::tickLevel);
        ClientEntityEvents.ENTITY_LOAD.register(SkinmaticsClientEvents::entityLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(SkinmaticsClientEvents::entityUnload);
        ClientCommandRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerCommands);
    }

    private static CompletableFuture<Void> onResourceReload(PreparableReloadListener.SharedState sharedState, Executor executor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor applyExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait).thenRunAsync(SkinmaticsTextureManager::reload, applyExecutor);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerPlayerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> livingEntityRenderer, LivingEntityRenderLayerRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if (livingEntityRenderer instanceof AvatarRenderer avatarRenderer) {
            registrationHelper.register(new EyesLayer(avatarRenderer));
            registrationHelper.register(new EmissiveSkinLayer(avatarRenderer));
        }
    }

    private static void tickLevel(ClientLevel level) {
        Entity origin = ProfileUtils.getViewingEntity();
        if (origin == null) return;

        RandomSource random = level.getRandom();
        BlockPos pos0 = origin.getOnPos();

        int updateRange = SkinmaticsConfig.getInstance().getUpdateRange();

        for (ProfileTracker entityTracker : TrackedProfileManager.TRACKED.values()) {
            Entity entity = entityTracker.getEntity(level);
            Profile profile = entityTracker.getProfile();

            if (entity == null) {
                TrackedProfileManager.untrack(entityTracker.getUUID());
                continue;
            }

            if (profile.eyes().blinkingChance() <= 0 || entity.getOnPos().distSqr(pos0) > (updateRange * updateRange)) continue;
            if (random.nextInt(profile.eyes().blinkingChance()) == 0) entityTracker.blink(entity.tickCount);
        }
    }

    private static void tickClient(Minecraft minecraft) {
        PaperDollRenderer.INSTANCE.tick();
    }

    private static void entityLoad(Entity entity, ClientLevel level) {
        String uuid = entity.getUUID().toString();
        String name = entity.getPlainTextName();

        Map<String, String> autoAssigns = SkinmaticsConfig.getInstance().getAutoAssigns();
        String target = null;

        if (autoAssigns.containsKey(uuid)) {
            target = uuid;
        } else if (autoAssigns.containsKey(name)) {
            target = name;
        }

        if (target != null) TrackedProfileManager.track(entity, ProfileManager.getOrLoad(autoAssigns.get(target)));
    }

    private static void entityUnload(Entity entity, ClientLevel level) {
        ProfileManager.unload(true);
    }

    private static void connected(ClientPacketListener listener, PacketSender sender, Minecraft minecraft) {
        SkinmaticsConfig.load();

        if (minecraft.player != null) {
            TrackedProfileManager.track(minecraft.player, ProfileManager.load(SkinmaticsConfig.getInstance().getProfile()));
        }
    }

    private static void disconnected(ClientPacketListener listener, Minecraft minecraft) {
        TrackedProfileManager.TRACKED.keySet().forEach(TrackedProfileManager::untrack);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
    }
}