package dev.proststuff.skinmatics.client.skinmatics.avatar.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.client.skinmatics.avatar.texture.Animatable;

public final class Skin extends SimpleCustomizationData {
    public static final Codec<Skin> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("slim").forGetter(Skin::slim),
                    Codec.BOOL.fieldOf("custom").forGetter(Skin::isCustom),
                    Codec.BOOL.fieldOf("emissive").forGetter(Skin::isEmissive),
                    Animatable.CODEC.fieldOf("textures").forGetter(Skin::getTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Skin::getEmissiveTextures)
            ).apply(inst, Skin::new)
    );

    private final boolean slim;

    public Skin(boolean slim, boolean custom, boolean emissive, Animatable textures, Animatable emissiveTextures) {
        super(custom, emissive, textures, emissiveTextures);
        this.slim = slim;
    }

    public Skin() {
        this(false, false, false, Animatable.empty(), Animatable.empty());
    }

    public boolean slim() {
        return slim;
    }
}