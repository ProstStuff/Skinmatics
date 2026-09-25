package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.v1.AvatarPack;

import java.util.HashMap;
import java.util.Map;

public final class AvatarPackManager implements Manageable<String, AvatarPack> {
    public static final AvatarPackManager INSTANCE = new AvatarPackManager();

    private final Map<String, AvatarPack> packs = new HashMap<>();
    private final Map<AvatarPack, String> inversePacks = new HashMap<>();

    private AvatarPackManager() {}

    @Override
    public Map<String, AvatarPack> getAll() {
        return packs;
    }

    @Override
    public Map<AvatarPack, String> getInverse() {
        return inversePacks;
    }

    @Override
    public AvatarPack register(String key, AvatarPack value) {
        Skinmatics.LOGGER.info("Registering pack {}", key);
        value.textures().forEach(AvatarTextureManager.INSTANCE::register);
        value.avatars().forEach(AvatarDataManager.INSTANCE::register);

        return Manageable.super.register(key, value);
    }
}
