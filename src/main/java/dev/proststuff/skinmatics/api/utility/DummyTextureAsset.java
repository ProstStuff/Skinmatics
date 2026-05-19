package dev.proststuff.skinmatics.api.utility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class DummyTextureAsset implements ClientAsset.Texture {
    public static DummyTextureAsset INSTANCE = new DummyTextureAsset();

    private DummyTextureAsset() {}

    @Override
    public @NonNull Identifier texturePath() {
        return MissingTextureAtlasSprite.getLocation();
    }

    @Override
    public @NonNull Identifier id() {
        return MissingTextureAtlasSprite.getLocation();
    }
}
