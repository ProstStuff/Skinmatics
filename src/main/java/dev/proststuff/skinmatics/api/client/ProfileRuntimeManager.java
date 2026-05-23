package dev.proststuff.skinmatics.api.client;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ProfileRuntimeManager {
    private static final Map<UUID, ProfileRuntime> RUNTIMES = new LinkedHashMap<>();
    private static final Map<Integer, UUID> ID_TO_UUID = new LinkedHashMap<>();

    public static ProfileData getCurrentProfileData() {
        Profile profile = ProfileManager.getCurrent();
        return profile != null ? profile.data : null;
    }

    public static ProfileRuntime get(UUID uuid) {
        return RUNTIMES.get(uuid);
    }

    public static ProfileRuntime get(int id) {
        if (!ID_TO_UUID.containsKey(id)) return null;
        return RUNTIMES.get(ID_TO_UUID.get(id));
    }

    public static ProfileRuntime get(AvatarRenderState state) {
        return get(state.id);
    }

    public static ProfileRuntime get(Entity entity) {
        return RUNTIMES.get(ID_TO_UUID.getOrDefault(entity.getId(), entity.getUUID()));
    }

    public static ProfileRuntime create(UUID uuid, int id, Profile profile) {
        ProfileRuntime runtime = RUNTIMES.computeIfAbsent(uuid, (_) -> new ProfileRuntime(uuid, id));
        runtime.setProfile(profile);
        ID_TO_UUID.values().removeIf(uuid::equals);
        ID_TO_UUID.put(id, uuid);
        return runtime;
    }

    public static ProfileRuntime create(Entity entity, Profile profile) {
        return create(entity.getUUID(), entity.getId(), profile);
    }

    public static ProfileRuntime remove(UUID uuid) {
        ID_TO_UUID.values().removeIf(uuid::equals);
        return RUNTIMES.remove(uuid);
    }

    public static void clear() {
        ID_TO_UUID.clear();
        RUNTIMES.clear();
    }
}
