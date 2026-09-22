package dev.proststuff.skinmatics.client.avatar.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.client.avatar.texture.Animatable;

public final class Cape extends SimpleCustomizationData {
    public static final Codec<Cape> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("visible").forGetter(Cape::isVisible),
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Cape::isCustomBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Cape::isEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Cape::getBaseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Cape::getEmissiveTextures)
            ).apply(inst, Cape::new)
    );

    public boolean visible;

    public Cape(boolean visible, boolean custom, boolean emissive, Animatable textures, Animatable emissiveTextures) {
        super(custom, emissive, textures, emissiveTextures);
        this.visible = visible;
    }

    public Cape() {
        this(true, false, false, Animatable.empty(), Animatable.empty());
    }

    public boolean isVisible() {
        return visible;
    }
}