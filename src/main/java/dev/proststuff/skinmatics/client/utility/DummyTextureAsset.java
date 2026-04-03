package dev.proststuff.skinmatics.client.utility;

import net.minecraft.client.texture.MissingSprite;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public class DummyTextureAsset implements AssetInfo.TextureAsset {
    public static DummyTextureAsset INSTANCE = new DummyTextureAsset();

    private DummyTextureAsset() {}

    @Override
    public Identifier texturePath() {
        return MissingSprite.getMissingSpriteId();
    }

    @Override
    public Identifier id() {
        return MissingSprite.getMissingSpriteId();
    }
}
