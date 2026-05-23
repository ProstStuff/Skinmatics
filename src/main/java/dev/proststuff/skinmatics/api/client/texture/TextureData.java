package dev.proststuff.skinmatics.api.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record TextureData(String texturePath, TextureIdentifier textureIdentifier, int width, int height, TextureSource source) {
    public static final TextureData EMPTY = new TextureData("missingno", TextureIdentifier.EMPTY, 16, 16, TextureSource.RESOURCE);

    public boolean isEmpty() {
        return textureIdentifier.isEmpty();
    }

    public enum TextureSource {
        MOD,
        RESOURCE
    }
}