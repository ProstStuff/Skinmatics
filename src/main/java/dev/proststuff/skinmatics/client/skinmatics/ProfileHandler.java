package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.utilitary.utility.UtilitaryJsonUtils;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class ProfileHandler {
    private static final Logger LOGGER = SkinmaticsClient.LOGGER;
    private static final Map<String, Profile> PROFILES = new LinkedHashMap<>();
    private static final Map<UUID, Profile> ASSIGNED_PROFILES = new LinkedHashMap<>();

    private static final Map<UUID, ProfileRuntime> ASSIGNED_RUNTIME_BY_UUID = new LinkedHashMap<>();
    private static final Map<Integer, UUID> ENTITY_ID_TO_UUID = new LinkedHashMap<>();

    private static Profile currentProfile;

    public static ProfileRuntime getRuntime(UUID uuid) {
        return ASSIGNED_RUNTIME_BY_UUID.get(uuid);
    }

    public static ProfileRuntime getRuntime(int id) {
        UUID toUUID = ENTITY_ID_TO_UUID.get(id);
        return toUUID != null ? ASSIGNED_RUNTIME_BY_UUID.get(toUUID) : null;
    }

    public static ProfileRuntime getRuntime(AvatarRenderState state) {
        return getRuntime(state.id);
    }

    public static ProfileRuntime getRuntime(Entity entity) {
        ProfileRuntime runtimeByUUID = getRuntime(entity.getUUID());
        ProfileRuntime runtimeByID = getRuntime(entity.getId());

        return runtimeByUUID != null ? runtimeByUUID : runtimeByID;
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }

    public static ProfileData getCurrentProfileData() {
        return getCurrentProfile() != null ? getCurrentProfile().data : null;
    }

    public static void loadCurrentProfile(Entity entity) {
        currentProfile = loadProfile(SkinmaticsConfig.INSTANCE.profile.get());
        assignProfile(entity, currentProfile);
    }

    public static Path getPath() {
        return UtilitaryJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles");
    }

    public static void createDirectory() {
        try {
            Files.createDirectories(getPath());
        } catch (IOException e) {
            LOGGER.error("Failed to create profiles folder", e);
        }
    }

    public static Profile loadProfile(String name) {
        createDirectory();
        Profile profile = PROFILES.computeIfAbsent(name, Profile::new);
        profile.load();
        return profile;
    }

    public static Profile getProfile(String name) {
        return PROFILES.get(name);
    }

    public static void walkProfiles() {
        createDirectory();

        try (Stream<Path> paths = Files.walk(getPath())) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                String name = path.getFileName().toString().replace(".json", "");
                PROFILES.computeIfAbsent(name, Profile::new);
            });
        } catch (IOException e) {
            LOGGER.error("Failed to scan profiles folder", e);
        }
    }

    public static void loadProfiles() {
        for (Profile profile : PROFILES.values()) {
            profile.load();
        }
    }

    public static void saveProfiles() {
        for (Profile profile : PROFILES.values()) {
            profile.save();
        }
    }

    private static Profile assignProfile(UUID uuid, Profile profile) {
        return ASSIGNED_PROFILES.put(uuid, profile);
    }

    public static Profile assignProfile(Entity entity, Profile profile) {
        UUID uuid = entity.getUUID();
        ASSIGNED_RUNTIME_BY_UUID.computeIfAbsent(entity.getUUID(), (_) -> new ProfileRuntime(profile, entity));
        ENTITY_ID_TO_UUID.put(entity.getId(), uuid);

        return assignProfile(uuid, profile);
    }

    public static Profile unassignProfile(UUID uuid) {
        ASSIGNED_RUNTIME_BY_UUID.remove(uuid);
        return ASSIGNED_PROFILES.remove(uuid);
    }

    public static Profile unassignProfile(Entity entity) {
        return unassignProfile(entity.getUUID());
    }

    public static Map<String, Profile> getProfiles() {
        return PROFILES;
    }

    public static Map<UUID, Profile> getAssignedProfiles() {
        return ASSIGNED_PROFILES;
    }

    public static void cleanUp(ClientLevel level) {
        Set<UUID> activeUUIDs = new HashSet<>();

        level.entitiesForRendering().forEach(entity -> {
            if (entity instanceof ClientAvatarEntity) {
                activeUUIDs.add(entity.getUUID());
            }
        });
        ASSIGNED_PROFILES.keySet()
                .removeIf(uuid -> {
                    boolean remove = !activeUUIDs.contains(uuid);
                    if (remove) ASSIGNED_RUNTIME_BY_UUID.remove(uuid);
                    return remove;
                });

        ENTITY_ID_TO_UUID.values().removeIf(uuid -> !activeUUIDs.contains(uuid));
    }

    public static void clear() {
        ASSIGNED_PROFILES.clear();
        ASSIGNED_RUNTIME_BY_UUID.clear();
        ENTITY_ID_TO_UUID.clear();
    }

    public static Profile getAssignedProfile(UUID uuid) {
        return ASSIGNED_PROFILES.get(uuid);
    }

    public static Profile getAssignedProfile(Entity entity) {
        return getAssignedProfile(entity.getUUID());
    }
}
