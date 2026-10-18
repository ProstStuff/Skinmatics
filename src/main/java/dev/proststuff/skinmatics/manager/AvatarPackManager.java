package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.AvatarPack;
import dev.proststuff.utilitary.registry.RemovableSimpleRegistry;

public class AvatarPackManager extends RemovableSimpleRegistry<String, AvatarPack> {
    public static final AvatarPackManager INSTANCE = new AvatarPackManager();

    private AvatarPackManager() {}

    @Override
    public AvatarPack register(String key, AvatarPack value) {
        Skinmatics.LOGGER.info("Registering pack {}", key);
        value.textures().forEach(AvatarTextureManager.INSTANCE::register);
        value.avatars().forEach(AvatarProfileManager.INSTANCE::register);

        return super.register(key, value);
    }
}
