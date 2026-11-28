package com.fraynn.skinmatics;

import com.fraynn.skinmatics.avatar.AvatarProfileManager;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import com.fraynn.skinmatics.avatar.AvatarUtils;
import com.fraynn.skinmatics.avatar.event.AvatarTrackerTickEvents;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import com.fraynn.skinmatics.entity.AvatarPlayer;
import com.fraynn.skinmatics.gui.PaperDollRenderer;
import com.fraynn.skinmatics.gui.screen.SkinmaticsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface SkinmaticsEvents {
    private static void tickTracker(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) {
        boolean canceled = AvatarTrackerTickEvents.PRE.invoker().tick(entity, tracker, isAvatarPlayer);

        if (!canceled) {
            tracker.tick();
            AvatarTrackerTickEvents.POST.invoker().tick(entity, tracker, isAvatarPlayer);
        }

        tracker.tickCount++;
    }

    static void tickLevel() {
        Entity origin = AvatarUtils.getClientEntity();
        if (origin == null) return;

        BlockPos pos0 = origin.getOnPos();
        int updateRange = SkinmaticsConfig.getInstance().getUpdateRange();

        for (AvatarTracker tracker : List.copyOf(AvatarTrackerManager.INSTANCE.values())) {
            LivingEntity entity = tracker.getEntity();

            if (entity instanceof AvatarPlayer) continue;

            if (tracker.entityUnloaded) {
                tracker.unloadedTickCount++;

                if (tracker.unloadedTickCount >= SkinmaticsConfig.getInstance().getUnloadedEntityTimeout()) {
                    AvatarTrackerManager.INSTANCE.unregisterValue(tracker);
                }

                continue;
            }

            if (entity == null || entity.isDeadOrDying()) {
                continue;
            }

            if (entity.getOnPos().distSqr(pos0) <= (updateRange * updateRange)) {
                tickTracker(entity, tracker, false);
            }
        }
    }

    static void tickClient(Minecraft minecraft) {
        PaperDollRenderer.INSTANCE.tick();

        if (SkinmaticsKeyMappings.OPEN_PROFILES.get().consumeClick()) {
            minecraft.setScreenAndShow(SkinmaticsGui.openScreen(minecraft.gui.screen()));
        }

        if (minecraft.player != null) {
            if (minecraft.crosshairPickEntity instanceof ClientAvatarEntity clientAvatar) {
                if (SkinmaticsKeyMappings.SET_PROFILE_ENTITY.get().consumeClick()) {
                    AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(minecraft.player);

                    if (tracker != null) {
                        AvatarTrackerManager.INSTANCE.register(minecraft.crosshairPickEntity.getUUID(), tracker.getAvatar());
                        minecraft.player.sendOverlayMessage(Component.translatable("skinmatics.set_avatar", tracker.getAvatar().name(), minecraft.crosshairPickEntity.getDisplayName()));
                    }
                }

                if (SkinmaticsKeyMappings.UNSET_PROFILE_ENTITY.get().consumeClick()) {
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
            avatarPlayer.tickCount++;

            if (tracker != null) {
                tickTracker(avatarPlayer, tracker, true);
            }
        }
    }

    static void entityLoad(Entity entity) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);

        if (AvatarUtils.isClientEntity(entity) && entity instanceof LivingEntity livingEntity) {
            tracker = AvatarTrackerManager.INSTANCE.register(livingEntity, AvatarProfileManager.INSTANCE.get(SkinmaticsConfig.getInstance().getAvatar()));
        }

        if (tracker != null) {
            tracker.entityUnloaded = false;
            tracker.unloadedTickCount = 0;
        }
    }

    static void entityUnload(Entity entity) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);

        if (tracker != null) {
            tracker.entityUnloaded = true;
        }
    }

    static void connected(Minecraft minecraft) {
        SkinmaticsConfig.load();

        if (minecraft.player != null) {
            AvatarTrackerManager.INSTANCE.register(minecraft.player, AvatarProfileManager.INSTANCE.get(SkinmaticsConfig.getInstance().getAvatar()));
        }
    }

    static void disconnected(Minecraft minecraft) {
        AvatarTrackerManager.INSTANCE.unregisterAll();
    }
}