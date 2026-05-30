package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.gui.customization.avatar.AvatarGUIRenderer;
import dev.proststuff.skinmatics.api.client.impl.mixin.ProfileHolder;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
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
        ProfileRuntimeManager.create(uuid, profile);
        return ASSIGNED_PROFILES.put(uuid, profile);
    }

    public static Profile assign(Entity entity, Profile profile) {
        EntityRenderer<?, ?> entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (entityRenderer instanceof ProfileHolder profileHolder) {
            profileHolder.skinmatics$setProfile(profile);
        }

        return assign(entity.getUUID(), profile);
    }

    public static Profile unassign(UUID uuid) {
        ProfileRuntimeManager.remove(uuid);
        return ASSIGNED_PROFILES.remove(uuid);
    }

    public static Profile unassign(Entity entity) {
        EntityRenderer<?, ?> entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        if (entityRenderer instanceof ProfileHolder profileHolder) {
            profileHolder.skinmatics$setProfile(null);
        }

        return unassign(entity.getUUID());
    }

    public static Profile load(String name) {
        createDirectory();
        Profile profile = PROFILES.computeIfAbsent(name, Profile::new);
        profile.load();
        return profile;
    }

    public static Profile loadCurrent(UUID uuid) {
        current = load(SkinmaticsConfig.INSTANCE.profile.get());
        assign(uuid, current);
        return current;
    }

    public static Profile loadCurrent(Entity entity) {
        current = load(SkinmaticsConfig.INSTANCE.profile.get());
        assign(entity, current);
        return current;
    }

    public static Profile loadCurrent() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer != null) {
            return loadCurrent(localPlayer);
        } else {
            return loadCurrent(getLocalUUID());
        }
    }

    public static Profile getCurrent() {
        return current;
    }

    public static void clear() {
        //AUTO_ASSIGNS.clear();
        ASSIGNED_PROFILES.values().removeIf((profile -> !profile.getProfileName().equals(current.getProfileName())));
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

    public static Map<String, Profile> scanAndLoad() {
        scan();
        loadAll();
        loadCurrent();
        return getAll();
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

        if (AvatarGUIRenderer.getPreviewer() != null) {
            activeUUIDs.add(AvatarGUIRenderer.getPreviewer().getUUID());
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

    public static UUID getLocalUUID() {
        return Minecraft.getInstance().getGameProfile().id();
    }

    public static boolean isLocalPlayer(Entity entity) {
        return Minecraft.getInstance().player != null ? entity.is(Minecraft.getInstance().player) : entity.getUUID() == getLocalUUID();
    }
}
