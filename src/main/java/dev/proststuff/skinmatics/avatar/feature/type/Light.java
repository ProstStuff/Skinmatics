package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Light(int strength) {
    public static final Codec<Light> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.intRange(0, 15).fieldOf("strength").forGetter(Light::strength)
            ).apply(inst, Light::new));

}
