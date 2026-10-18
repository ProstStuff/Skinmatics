package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.feature.Animatable;

public record Cape(boolean customBaseTexture, boolean emissive, boolean strongerEmissive, Animatable baseTextures, Animatable emissiveTextures) {
    public static final Codec<Cape> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Cape::customBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Cape::emissive),
                    Codec.BOOL.fieldOf("strongerEmissive").forGetter(Cape::strongerEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Cape::baseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Cape::emissiveTextures)
            ).apply(inst, Cape::new)
    );
}
