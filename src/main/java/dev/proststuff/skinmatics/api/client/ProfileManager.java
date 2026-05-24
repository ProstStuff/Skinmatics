package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public final class ProfileManager {
    private static final Logger LOGGER = SkinmaticsClient.LOGGER;
    private static final Map<String, Profile> PROFILES = new LinkedHashMap<>();
    private static final Map<UUID, String> AUTO_ASSIGNS = new LinkedHashMap<>();
    private static final Map<UUID, Profile> ASSIGNED_PROFILES = new LinkedHashMap<>();

    private static Profile current;

    public static Map<String, Profile> getAll() {
        return PROFILES;
    }

    public static Map<UUID, Profile> getAllAssigned() {
        return ASSIGNED_PROFILES;
    }

    public static Map<UUID, String> getAutoAssigns() {
        return AUTO_ASSIGNS;
    }

    public static Profile get(String name) {
        return PROFILES.get(name);
    }

    public static Profile getAssigned(UUID uuid) {
        return ASSIGNED_PROFILES.get(uuid);
    }

    public static Profile getAssigned(Entity entity) {
        return getAssigned(entity.getUUID());
    }

    public static Profile assign(UUID uuid, Profile profile) {
        AUTO_ASSIGNS.put(uuid, profile.getProfileName());
        return ASSIGNED_PROFILES.put(uuid, profile);
    }

    public static Profile assign(Entity entity, Profile profile) {
        UUID uuid = entity.getUUID();
        ProfileRuntimeManager.create(entity, profile);
        return assign(uuid, profile);
    }

    public static Profile unassign(UUID uuid) {
        ProfileRuntimeManager.remove(uuid);
        return ASSIGNED_PROFILES.remove(uuid);
    }

    public static Profile unassign(Entity entity) {
        return unassign(entity.getUUID());
    }

    public static Profile load(String name) {
        createDirectory();
        Profile profile = PROFILES.computeIfAbsent(name, Profile::new);
        profile.load();
        return profile;
    }

    public static Profile loadCurrent(Entity entity) {
        if (entity == null) return null;

        current = load(SkinmaticsConfig.INSTANCE.profile.get());
        assign(entity, current);
        return current;
    }

    public static Profile loadCurrent() {
        return loadCurrent(Minecraft.getInstance().player);
    }

    public static Profile getCurrent() {
        return current;
    }

    public static void clear() {
        //AUTO_ASSIGNS.clear();
        ASSIGNED_PROFILES.clear();
        ProfileRuntimeManager.clear();
    }

    public static void scan() {
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

    public static void loadAll() {
        for (Profile profile : PROFILES.values()) {
            profile.load();
        }
    }

    public static void saveAll() {
        for (Profile profile : PROFILES.values()) {
            profile.save();
        }
    }

    public static void scanAndLoad() {
        scan();
        loadAll();
        loadCurrent();
    }

    public static Path getPath() {
        return FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles");
    }

    public static void createDirectory() {
        try {
            Files.createDirectories(getPath());
        } catch (IOException e) {
            LOGGER.error("Failed to create profiles folder", e);
        }
    }

    public static void cleanUp(ClientLevel level) {
        Set<UUID> activeUUIDs = new HashSet<>();

        if (AvatarPreviewRenderer.PREVIEWER != null) {
            activeUUIDs.add(AvatarPreviewRenderer.PREVIEWER.getUUID());
        }

        level.entitiesForRendering().forEach(entity -> {
            if (entity instanceof ClientAvatarEntity) activeUUIDs.add(entity.getUUID());
        });

        ASSIGNED_PROFILES.keySet().removeIf(uuid -> {
            boolean remove = !activeUUIDs.contains(uuid);
            if (remove) ProfileRuntimeManager.remove(uuid);
            return remove;
        });
    }
}
