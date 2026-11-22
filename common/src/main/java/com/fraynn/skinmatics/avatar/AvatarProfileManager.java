package com.fraynn.skinmatics.avatar;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.pack.AvatarPack;
import com.fraynn.utilitary.platform.services.IPlatformHelper;
import com.fraynn.utilitary.registry.RemovableSimpleRegistry;
import com.fraynn.utilitary.util.Either;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AvatarProfileManager extends RemovableSimpleRegistry<String, AvatarProfile> {
    public static final Path PATH = IPlatformHelper.INSTANCE.getConfigPath().resolve(Skinmatics.ID).resolve("avatars");
    public static final AvatarProfileManager INSTANCE = new AvatarProfileManager();

    private final Map<String, String> failures = new HashMap<>();
    private final Map<String, AvatarProfile> fallbacks = new HashMap<>();

    private AvatarProfileManager() {}

    public Map<String, String> getFailures() {
        return failures;
    }

    @Override
    public @Nullable AvatarProfile get(String key) {
        AvatarProfile avatarProfile = super.get(key);

        if (avatarProfile == null) {
            return fallbacks.get(key);
        }

        return avatarProfile;
    }

    @Override
    public AvatarProfile unregister(String key) {
        AvatarProfile avatarProfile = super.unregister(key);
        AvatarTrackerManager.INSTANCE.unregisterAll(avatarProfile);
        return avatarProfile;
    }

    @Override
    public String unregisterValue(AvatarProfile value) {
        AvatarTrackerManager.INSTANCE.unregisterAll(value);
        return super.unregisterValue(value);
    }

    @Override
    public void unregisterAll() {
        fallbacks.clear();
        super.unregisterAll();
    }

    public Map<String, AvatarProfile> getFallbacks() {
        return fallbacks;
    }

    public void register(AvatarPack source, String name, Either<AvatarProfile, String> either) {
        either
                .ifLeft(profile -> {
                    register(name, profile);

                    if (source.locked()) {
                        fallbacks.put(name, profile);
                    }
                })
                .ifRight(err -> failures.put(name, err));
    }
}