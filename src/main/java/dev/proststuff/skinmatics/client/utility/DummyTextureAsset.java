package dev.proststuff.skinmatics.client.utility;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

public class DummyTextureAsset implements ClientAsset.Texture {
    public static DummyTextureAsset INSTANCE = new DummyTextureAsset();

    private DummyTextureAsset() {}

    @Override
    public Identifier texturePath() {
        return MissingTextureAtlasSprite.getLocation();
    }

    @Override
    public Identifier id() {
        return MissingTextureAtlasSprite.getLocation();
    }
}
