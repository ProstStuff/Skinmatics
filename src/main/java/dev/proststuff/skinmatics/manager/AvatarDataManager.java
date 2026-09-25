package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.v1.data.AvatarData;
import dev.proststuff.utilitary.api.v1.util.Either;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class AvatarDataManager implements Manageable<String, AvatarData> {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(Skinmatics.ID).resolve("avatars");
    public static final AvatarDataManager INSTANCE = new AvatarDataManager();

    private final Map<String, AvatarData> profiles = new HashMap<>();
    private final Map<AvatarData, String> inverseProfiles = new HashMap<>();
    private final Map<String, String> failures = new HashMap<>();

    private AvatarDataManager() {}

    @Override
    public Map<String, AvatarData> getAll() {
        return profiles;
    }

    @Override
    public Map<AvatarData, String> getInverse() {
        return inverseProfiles;
    }

    public Map<String, String> getFailures() {
        return failures;
    }

    public void register(String name, Either<AvatarData, String> either) {
        either
                .ifLeft(profile -> register(name, profile))
                .ifRight(err -> failures.put(name, err));
    }
}