package dev.proststuff.skinmatics.client.avatar.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.client.avatar.texture.Animatable;

public final class Wings extends SimpleCustomizationData {
    public static final Codec<Wings> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Wings::isCustomBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Wings::isEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Wings::getBaseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Wings::getEmissiveTextures)
            ).apply(inst, Wings::new)
    );

    public Wings(boolean custom, boolean emissive, Animatable textures, Animatable emissiveTextures) {
        super(custom, emissive, textures, emissiveTextures);
    }

    public Wings() {
        this(false, false, Animatable.empty(), Animatable.empty());
    }
}
