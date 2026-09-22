package dev.proststuff.skinmatics.client.avatar.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public final class Lights {
    public static final Codec<Lights> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.intRange(0, 15).fieldOf("strength").forGetter(Lights::get)
            ).apply(inst, Lights::new)
    );

    public int strength;

    public Lights(int strength) {
        this.strength = strength;
    }

    public Lights() {
        this(0);
    }

    public int get() {
        return Mth.clamp(strength, 0, 15);
    }
}