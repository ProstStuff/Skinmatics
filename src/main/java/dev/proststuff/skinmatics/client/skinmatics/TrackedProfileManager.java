package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TrackedProfileManager {
    public static final Map<UUID, ProfileTracker> TRACKED = new HashMap<>();

    public static ProfileTracker get(Entity entity) {
        return TRACKED.get(entity.getUUID());
    }

    public static ProfileTracker getViewing() {
        return TRACKED.get(ProfileUtils.getViewingUUID());
    }

    public static ProfileTracker track(Entity entity, Profile profile) {
        ProfileTracker entityTracker = TRACKED.computeIfAbsent(entity.getUUID(), (_) -> new ProfileTracker(entity, profile));
        entityTracker.setProfile(profile);
        return entityTracker;
    }

    public static ProfileTracker trackClient(Profile profile) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) return track(player, profile);
        return null;
    }

    public static ProfileTracker untrack(UUID uuid) {
        return TRACKED.remove(uuid);
    }

    public static void untrackAll() {
        TRACKED.clear();
    }

    private TrackedProfileManager() {}
}