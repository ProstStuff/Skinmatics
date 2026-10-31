package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.avatar.AvatarPackLoader;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.avatar.event.AvatarTrackerTickEvents;
import dev.proststuff.skinmatics.avatar.manager.AvatarProfileManager;
import dev.proststuff.skinmatics.avatar.manager.AvatarTrackerManager;
import dev.proststuff.skinmatics.avatar.renderer.PaperDollRenderer;
import dev.proststuff.skinmatics.avatar.renderer.layer.EmissiveSkinRenderLayer;
import dev.proststuff.skinmatics.avatar.renderer.layer.EyesRenderLayer;
import dev.proststuff.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.entity.AvatarPlayer;
import dev.proststuff.skinmatics.gui.SkinmaticsGui;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface SkinmaticsClientEvents {
    static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsClientEvents::tickClient);

        ClientPlayConnectionEvents.JOIN.register(SkinmaticsClientEvents::connected);
        ClientPlayConnectionEvents.DISCONNECT.register(SkinmaticsClientEvents::disconnected);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerPlayerFeatures);
        ClientTickEvents.END_LEVEL_TICK.register(SkinmaticsClientEvents::tickLevel);
        ClientEntityEvents.ENTITY_LOAD.register(SkinmaticsClientEvents::entityLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(SkinmaticsClientEvents::entityUnload);
        SkinmaticsKeyMappings.init();
        AvatarPackLoader.init();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerPlayerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> livingEntityRenderer, LivingEntityRenderLayerRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if (livingEntityRenderer instanceof AvatarRenderer avatarRenderer) {
            registrationHelper.register(new EyesRenderLayer(avatarRenderer));
            registrationHelper.register(new EmissiveSkinRenderLayer(avatarRenderer));
        }
    }

    private static void tickTracker(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) {
        boolean canceled = AvatarTrackerTickEvents.PRE.invoker().tick(entity, tracker, isAvatarPlayer);

        if (!canceled) {
            tracker.tick();
            AvatarTrackerTickEvents.POST.invoker().tick(entity, tracker, isAvatarPlayer);
        }

        tracker.tickCount ++;
    }

    private static void tickLevel(ClientLevel level) {
        Entity origin = AvatarUtils.getClientEntity();
        if (origin == null) return;

        BlockPos pos0 = origin.getOnPos();
        int updateRange = SkinmaticsConfig.getInstance().getUpdateRange();

        for (AvatarTracker tracker : List.copyOf(AvatarTrackerManager.INSTANCE.values())) {
            LivingEntity entity = tracker.getEntity();

            if (entity instanceof AvatarPlayer) continue;

            if (tracker.entityUnloaded) {
                tracker.unloadedTickCount ++;

                if (tracker.unloadedTickCount >= SkinmaticsConfig.getInstance().getUnloadedEntityTimeout()) {
                    AvatarTrackerManager.INSTANCE.unregisterValue(tracker);
                }

                continue;
            }

            if (entity == null || entity.isDeadOrDying()) {
                AvatarTrackerManager.INSTANCE.unregisterValue(tracker);
                continue;
            }

            if (entity.getOnPos().distSqr(pos0) <= (updateRange * updateRange)) {
                tickTracker(entity, tracker, false);
            }
        }
    }

    private static void tickClient(Minecraft minecraft) {
        PaperDollRenderer.INSTANCE.tick();

        if (SkinmaticsKeyMappings.OPEN_PROFILES.consumeClick()) {
            minecraft.setScreen(SkinmaticsGui.openScreen(minecraft.screen));
        }

        if (minecraft.player != null) {
            if (minecraft.crosshairPickEntity instanceof ClientAvatarEntity clientAvatar) {
                if (SkinmaticsKeyMappings.SET_PROFILE_ENTITY.consumeClick()) {
                    AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(minecraft.player);

                    if (tracker != null) {
                        AvatarTrackerManager.INSTANCE.register(minecraft.crosshairPickEntity.getUUID(), tracker.getAvatar());
                        minecraft.player.sendOverlayMessage(Component.translatable("skinmatics.set_avatar", tracker.getAvatar().name(), minecraft.crosshairPickEntity.getDisplayName()));
                    }
                }

                if (SkinmaticsKeyMappings.UNSET_PROFILE_ENTITY.consumeClick()) {
                    AvatarTracker tracker = AvatarTrackerManager.INSTANCE.unregister(minecraft.crosshairPickEntity.getUUID());
                    if (tracker != null)
                        minecraft.player.sendOverlayMessage(Component.translatable("skinmatics.unset_avatar", tracker.getAvatar().name(), minecraft.crosshairPickEntity.getDisplayName()));
                }
            }
        }

        if (minecraft.level != null && AvatarPlayer.hasInstance()) {
            AvatarPlayer avatarPlayer = AvatarPlayer.getInstance();
            AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(avatarPlayer);

            avatarPlayer.tick();
            avatarPlayer.tickCount ++;

            if (tracker != null) {
                tickTracker(avatarPlayer, tracker, true);
            }
        }
    }

    private static void entityLoad(Entity entity, ClientLevel level) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);

        if (AvatarUtils.isClientEntity(entity) && entity instanceof LivingEntity livingEntity) {
            tracker = AvatarTrackerManager.INSTANCE.register(livingEntity, AvatarProfileManager.INSTANCE.get(SkinmaticsConfig.getInstance().getAvatar()));
        }

        if (tracker != null) {
            tracker.entityUnloaded = false;
            tracker.unloadedTickCount = 0;
        }
    }

    private static void entityUnload(Entity entity, ClientLevel level) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);

        if (tracker != null) {
            tracker.entityUnloaded = true;
        }
    }

    private static void connected(ClientPacketListener listener, PacketSender sender, Minecraft minecraft) {
        SkinmaticsConfig.load();

        if (minecraft.player != null) {
            AvatarTrackerManager.INSTANCE.register(minecraft.player, AvatarProfileManager.INSTANCE.get(SkinmaticsConfig.getInstance().getAvatar()));
        }
    }

    private static void disconnected(ClientPacketListener listener, Minecraft minecraft) {
        AvatarTrackerManager.INSTANCE.unregisterAll();
    }
}