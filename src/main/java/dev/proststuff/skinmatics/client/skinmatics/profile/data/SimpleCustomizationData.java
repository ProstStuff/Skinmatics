package dev.proststuff.skinmatics.client.skinmatics.profile.data;

import dev.proststuff.skinmatics.client.skinmatics.texture.Animatable;
import org.jspecify.annotations.NonNull;

public abstract class SimpleCustomizationData {
    public boolean custom;
    public boolean emissive;
    protected final @NonNull Animatable textures;
    protected final @NonNull Animatable emissiveTextures;

    public SimpleCustomizationData(boolean custom, boolean emissive, @NonNull Animatable textures, @NonNull Animatable emissiveTextures) {
        this.custom = custom;
        this.emissive = emissive;
        this.textures = textures;
        this.emissiveTextures = emissiveTextures;
    }

    public boolean isCustom() {
        return custom;
    }

    public boolean isEmissive() {
        return emissive;
    }

    public @NonNull Animatable getTextures() {
        return textures;
    }

    public @NonNull Animatable getEmissiveTextures() {
        return emissiveTextures;
    }
}
