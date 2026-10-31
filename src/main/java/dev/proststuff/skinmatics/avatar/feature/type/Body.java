package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.feature.Animatable;
import net.minecraft.world.phys.Vec2;

public record Body(boolean slim, Vec2 scale, boolean customBaseTexture, boolean emissive, boolean strongerEmissive, Animatable baseTextures, Animatable emissiveTextures) {
    public static final Codec<Body> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("slim").forGetter(Body::slim),
                    Vec2.CODEC.fieldOf("scale").forGetter(Body::scale),
                    Codec.BOOL.fieldOf("customBaseTexture").forGetter(Body::customBaseTexture),
                    Codec.BOOL.fieldOf("emissive").forGetter(Body::emissive),
                    Codec.BOOL.fieldOf("strongerEmissive").forGetter(Body::strongerEmissive),
                    Animatable.CODEC.fieldOf("baseTextures").forGetter(Body::baseTextures),
                    Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Body::emissiveTextures)
            ).apply(inst, Body::new)
    );
}
