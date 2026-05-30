package dev.proststuff.skinmatics.api.client.impl;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SkinmaticsClientMod {
    default SkinmaticsTextureManager textureManager() {
        return SkinmaticsTextureManager.INSTANCE;
    }

    default SkinmaticsConfig config() {
        return SkinmaticsConfig.INSTANCE;
    }
}
