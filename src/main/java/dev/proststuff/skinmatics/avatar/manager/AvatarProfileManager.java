package dev.proststuff.skinmatics.avatar.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.utilitary.registry.RemovableSimpleRegistry;
import dev.proststuff.utilitary.util.Either;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AvatarProfileManager extends RemovableSimpleRegistry<String, AvatarProfile> {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(Skinmatics.ID).resolve("avatars");
    public static final AvatarProfileManager INSTANCE = new AvatarProfileManager();

    private final Map<String, String> failures = new HashMap<>();

    private AvatarProfileManager() {}

    public Map<String, String> getFailures() {
        return failures;
    }

    public void register(String name, Either<AvatarProfile, String> either) {
        either
                .ifLeft(profile -> register(name, profile))
                .ifRight(err -> failures.put(name, err));
    }
}