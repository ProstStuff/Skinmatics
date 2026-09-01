package dev.proststuff.skinmatics.client.skinmatics.avatar.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.proststuff.skinmatics.client.skinmatics.avatar.texture.Animatable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public record Eyes(
        boolean visible,
        List<String> hidden,
        List<String> emissive,
        int blinkingChance,
        int blinkingTicks,
        Map<String, EyeTextureSet> textures,
        Map<String, EyeTextureSet> emissiveTextures
) {
    public static final UnboundedMapCodec<String, EyeTextureSet> GROUP_CODEC = Codec.unboundedMap(Codec.STRING, EyeTextureSet.CODEC);
    public static final Codec<Eyes> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("visible").forGetter(Eyes::visible),
                    Codec.STRING.listOf().fieldOf("hidden").forGetter(Eyes::hidden),
                    Codec.STRING.listOf().fieldOf("emissive").forGetter(Eyes::emissive),
                    Codec.INT.fieldOf("blinkingChance").forGetter(Eyes::blinkingChance),
                    Codec.INT.fieldOf("blinkingTicks").forGetter(Eyes::blinkingTicks),
                    GROUP_CODEC.fieldOf("textures").forGetter(Eyes::textures),
                    GROUP_CODEC.fieldOf("emissiveTextures").forGetter(Eyes::emissiveTextures)
            ).apply(inst, Eyes::new)
    );

    public Eyes() {
        this(false, new ArrayList<>(), new ArrayList<>(), 0, 0, new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public @NonNull EyeTextureSet get(String group, boolean emissive) {
        return emissive ? emissiveTextures.getOrDefault(group, EyeTextureSet.EMPTY) : textures.getOrDefault(group, EyeTextureSet.EMPTY);
    }

    public @NonNull Animatable get(String group, String state, boolean emissive) {
        return get(group, emissive).get(state);
    }

    public boolean isHidden(String group) {
        return hidden.contains(group);
    }

    public boolean isEmissive(String group) {
        return emissive.contains(group);
    }

    public record EyeTextureSet(Map<String, Animatable> states) {
        public static final EyeTextureSet EMPTY = new EyeTextureSet(new HashMap<>());
        public static final Codec<EyeTextureSet> CODEC = Codec.unboundedMap(Codec.STRING, Animatable.CODEC).xmap(EyeTextureSet::new, EyeTextureSet::states);

        public @NonNull Animatable get(String state) {
            return states.getOrDefault(state, Animatable.EMPTY);
        }

        public boolean isEmpty() {
            return this == EMPTY || states.isEmpty();
        }
    }
}
