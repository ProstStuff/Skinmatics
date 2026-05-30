package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.api.client.impl.mixin.UUIDHolder;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ProfileRuntimeManager {
    private static final Map<UUID, ProfileRuntime> RUNTIMES = new LinkedHashMap<>();

    public static ProfileData getCurrentProfileData() {
        Profile profile = ProfileManager.getCurrent();
        return profile != null ? profile.data : null;
    }

    public static Map<UUID, ProfileRuntime> getAll() {
        return RUNTIMES;
    }

    public static ProfileRuntime get(UUID uuid) {
        return RUNTIMES.get(uuid);
    }

    public static ProfileRuntime get(Entity entity) {
        return get(entity.getUUID());
    }

    public static ProfileRuntime get(AvatarRenderState state) {
        return get(((UUIDHolder) state).skinmatics$getUUID());
    }

    public static ProfileRuntime create(UUID uuid, Profile profile) {
        ProfileRuntime runtime = RUNTIMES.computeIfAbsent(uuid, ProfileRuntime::new);
        runtime.setProfile(profile);
        return runtime;
    }

    public static ProfileRuntime create(Entity entity, Profile profile) {
        return create(entity.getUUID(), profile);
    }

    public static ProfileRuntime remove(UUID uuid) {
        return RUNTIMES.remove(uuid);
    }

    public static void clear() {
        RUNTIMES.clear();
    }
}
