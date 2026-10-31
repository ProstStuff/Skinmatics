package dev.proststuff.skinmatics.avatar.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.AvatarPack;
import dev.proststuff.utilitary.registry.RemovableSimpleRegistry;

public class AvatarPackManager extends RemovableSimpleRegistry<String, AvatarPack> {
    public static final AvatarPackManager INSTANCE = new AvatarPackManager();

    private AvatarPackManager() {}

    @Override
    public AvatarPack register(String key, AvatarPack value) {
        Skinmatics.LOGGER.info("Registering pack '{}'", value.info().name().getString());
        value.textures().forEach(AvatarTextureManager.INSTANCE::register);
        value.avatars().forEach((a, b) -> AvatarProfileManager.INSTANCE.register(value, a, b));

        return super.register(key, value);
    }
}
