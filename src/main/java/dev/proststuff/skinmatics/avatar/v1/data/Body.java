package dev.proststuff.skinmatics.avatar.v1.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.v1.texture.Animatable;

public final class Body extends SimpleCustomizationData {
    public static final Codec<Body> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("slim").forGetter(Body::slim),
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Body::isCustomBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Body::isEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Body::getBaseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Body::getEmissiveTextures)
            ).apply(inst, Body::new)
    );

    private final boolean slim;

    public Body(boolean slim, boolean custom, boolean emissive, Animatable textures, Animatable emissiveTextures) {
        super(custom, emissive, textures, emissiveTextures);
        this.slim = slim;
    }

    public Body() {
        this(false, false, false, Animatable.empty(), Animatable.empty());
    }

    public boolean slim() {
        return slim;
    }
}