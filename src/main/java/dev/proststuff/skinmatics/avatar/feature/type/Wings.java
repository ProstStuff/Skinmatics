package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.feature.Animatable;

public record Wings(boolean customBaseTexture, boolean emissive, boolean strongerEmissive, Animatable baseTextures, Animatable emissiveTextures) {
    public static final Codec<Wings> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Wings::customBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Wings::emissive),
                    Codec.BOOL.fieldOf("strongerEmissive").forGetter(Wings::strongerEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Wings::baseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Wings::emissiveTextures)
            ).apply(inst, Wings::new)
    );
}
