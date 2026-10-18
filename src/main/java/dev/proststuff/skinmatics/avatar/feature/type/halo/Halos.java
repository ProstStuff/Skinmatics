package dev.proststuff.skinmatics.avatar.feature.type.halo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;

import java.util.Map;

public record Halos(
        Vec2 origin,
        float timeScale,
        Map<String, Ring> rings
) {
    public static final Codec<Halos> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Vec2.CODEC.fieldOf("origin").forGetter(Halos::origin),
                    Codec.FLOAT.fieldOf("timeScale").forGetter(Halos::timeScale),
                    Codec.unboundedMap(Codec.STRING, Ring.CODEC).fieldOf("rings").forGetter(Halos::rings)
            ).apply(inst, Halos::new)
    );
}
