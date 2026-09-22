package dev.proststuff.skinmatics.client.avatar.data;

import dev.proststuff.skinmatics.client.avatar.texture.Animatable;
import org.jspecify.annotations.NonNull;

public abstract class SimpleCustomizationData {
    public boolean customBaseTexture;
    public boolean emissive;
    protected final @NonNull Animatable baseTextures;
    protected final @NonNull Animatable emissiveTextures;

    public SimpleCustomizationData(boolean customBaseTexture, boolean emissive, @NonNull Animatable baseTextures, @NonNull Animatable emissiveTextures) {
        this.customBaseTexture = customBaseTexture;
        this.emissive = emissive;
        this.baseTextures = baseTextures;
        this.emissiveTextures = emissiveTextures;
    }

    public boolean isCustomBaseTexture() {
        return customBaseTexture;
    }

    public boolean isEmissive() {
        return emissive;
    }

    public @NonNull Animatable getBaseTextures() {
        return baseTextures;
    }

    public @NonNull Animatable getEmissiveTextures() {
        return emissiveTextures;
    }
}
