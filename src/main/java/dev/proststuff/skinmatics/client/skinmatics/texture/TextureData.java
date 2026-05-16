package dev.proststuff.skinmatics.client.skinmatics.texture;

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