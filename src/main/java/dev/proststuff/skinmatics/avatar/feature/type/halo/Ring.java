package dev.proststuff.skinmatics.avatar.feature.type.halo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.feature.Animatable;
import net.minecraft.world.phys.Vec3;

public record Ring(boolean strongerEmissive, float maxRange, Vec3 position, Vec3 rotation, Animatable textures) {
    public static final Codec<Ring> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.BOOL.fieldOf("strongerEmissive").forGetter(Ring::strongerEmissive),
                    Codec.FLOAT.fieldOf("maxRange").forGetter(Ring::maxRange),
                    Vec3.CODEC.fieldOf("position").forGetter(Ring::position),
                    Vec3.CODEC.fieldOf("rotation").forGetter(Ring::rotation),
                    Animatable.CODEC.fieldOf("textures").forGetter(Ring::textures)
            ).apply(inst, Ring::new)
    );
}