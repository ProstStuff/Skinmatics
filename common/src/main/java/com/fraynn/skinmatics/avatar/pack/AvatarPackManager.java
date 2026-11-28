package com.fraynn.skinmatics.avatar.pack;

import com.fraynn.skinmatics.avatar.AvatarProfileManager;
import com.fraynn.skinmatics.texture.AvatarTextureManager;
import com.fraynn.utilitary.registry.RemovableSimpleRegistry;

public class AvatarPackManager extends RemovableSimpleRegistry<String, AvatarPack> {
    public static final AvatarPackManager INSTANCE = new AvatarPackManager();

    private AvatarPackManager() {}

    @Override
    public AvatarPack register(String key, AvatarPack value) {
        value.textures().forEach(AvatarTextureManager.INSTANCE::register);
        value.avatars().forEach((a, b) -> AvatarProfileManager.INSTANCE.register(value, a, b));

        return super.register(key, value);
    }
}
