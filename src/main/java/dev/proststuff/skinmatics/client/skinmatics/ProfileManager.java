package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileMetadataType;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import dev.proststuff.utilitary.api.v1.util.Either;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import dev.proststuff.utilitary.config.v1.ConfigManager;
import dev.proststuff.utilitary.config.v1.ConfigResult;
import dev.proststuff.utilitary.config.v1.ConfigStatus;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ProfileManager {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(SkinmaticsMod.ID).resolve("profiles");
    public static final Set<String> PROFILE_FILES = new LinkedHashSet<>();
    public static final Map<String, Profile> LOADED_PROFILES = new LinkedHashMap<>();
    public static final Map<String, CompletableFuture<ConfigResult<Profile, ProfileMetadataType.Metadata>>> LOADING_PROFILES = new LinkedHashMap<>();

    private static final ExecutorService PROFILE_LOADER = Executors.newSingleThreadExecutor();

    public static void setClientProfile(String name) {
        SkinmaticsConfig.getInstance().setProfile(name);
    }
    
    public static @Nullable Profile getLoaded(String name) {
        return LOADED_PROFILES.get(name);
    }

    public static Profile getOrLoad(String name) {
        Profile profile = getLoaded(name);

        if (profile == null) {
            profile = load(name);
        }

        return profile;
    }

    public static Either<Profile, CompletableFuture<ConfigResult<Profile, ProfileMetadataType.Metadata>>> getOrLoadAsync(String name) {
        return LOADED_PROFILES.containsKey(name) ? Either.left(LOADED_PROFILES.get(name)) : Either.right(loadAsync(name));
    }

    public static ConfigResult<Profile, ProfileMetadataType.Metadata> read(String name) {
        SimpleIdentifier id = ProfileUtils.toSimpleIdentifier(name);
        return ConfigManager.load(id, Profile.TYPE);
    }

    public static Profile load(String name) {
        ConfigResult<Profile, ProfileMetadataType.Metadata> result = read(name);

        LOADED_PROFILES.put(name, result.config());
        return result.config();
    }

    public static CompletableFuture<ConfigResult<Profile, ProfileMetadataType.Metadata>> loadAsync(String name) {
        return LOADING_PROFILES.computeIfAbsent(name, (_) ->
                CompletableFuture.supplyAsync(() -> read(name), PROFILE_LOADER)
                        .whenCompleteAsync(
                                (profileMetadataResult, _) -> {
                                    LOADED_PROFILES.remove(name);
                                    LOADED_PROFILES.put(name, profileMetadataResult.config());
                                },
                                Minecraft.getInstance()
                        )
        );
    }

    public static boolean save(String name, Profile profile) {
        LOADED_PROFILES.put(name, profile);
        SimpleIdentifier id = ProfileUtils.toSimpleIdentifier(name);
        return ConfigManager.save(id, Profile.TYPE, profile) == ConfigStatus.SUCCESS;
    }

    public static boolean save(String name) {
        if (!LOADED_PROFILES.containsKey(name)) throw new IllegalArgumentException("Profile " + name + " not found");
        return save(name, LOADED_PROFILES.get(name));
    }

    public static boolean delete(String name) {
        LOADED_PROFILES.remove(name);
        return ConfigManager.delete(ProfileUtils.toSimpleIdentifier(name), Profile.TYPE);
    }

    public static void scan() {
        ProfileUtils.createDirectory();

        PROFILE_FILES.clear();

        try (Stream<Path> paths = Files.walk(PATH)) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                String name = PATH.relativize(path).toString().replace('\\', '/').replace(".json", "");
                PROFILE_FILES.add(name);
            });
        } catch (IOException e) {
            SkinmaticsMod.LOGGER.error("[SKINMATICS PROFILES] Failed to scan profile folder", e);
        }
    }

    public static void loadAll() {
        LOADED_PROFILES.keySet().forEach(ProfileManager::load);
    }

    public static void saveAll() {
        LOADED_PROFILES.keySet().forEach(ProfileManager::save);
    }

    public static void unload(boolean untrackAllTrackedEntities) {
        if (untrackAllTrackedEntities) {
            LOADED_PROFILES.clear();
            TrackedProfileManager.untrackAll();
            return;
        }

        Set<Profile> usedProfiles = TrackedProfileManager.TRACKED.values().stream().map(ProfileTracker::getProfile).collect(Collectors.toSet());
        LOADED_PROFILES.entrySet().removeIf(entry -> !usedProfiles.contains(entry.getValue()));
    }

    private ProfileManager() {}
}