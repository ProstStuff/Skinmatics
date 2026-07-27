package dev.proststuff.skinmatics.client.skinmatics.texture;

import com.mojang.serialization.Codec;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class Animatable {
    public static final Codec<Animatable> CODEC = Codec.unboundedMap(TextureData.CODEC, Codec.INT.listOf()).xmap(
            map -> {
                Animatable animatable = empty();

                map.forEach((texture, frames) -> {
                    for (int frame : frames) {
                        animatable.set(texture, frame);
                    }
                });

                return animatable;
            },
            animatable -> {
                Map<TextureData, List<Integer>> map = new LinkedHashMap<>();
                animatable.getTextures().forEach((frame, texture) -> map.computeIfAbsent(texture, t -> new ArrayList<>()).add(frame));
                return map;
            }
    );
    public static final Animatable EMPTY = empty();

    public static Animatable empty() {
        return new Animatable();
    }

    protected final NavigableMap<Integer, TextureData> textures;

    public Animatable() {
        textures = new TreeMap<>();
    }

    public @NonNull TextureData get(int at) {
        if (textures.containsKey(at)) return textures.get(at);

        Map.Entry<Integer, TextureData> nextTexture = textures.higherEntry(at);
        if (nextTexture != null) return nextTexture.getValue();
        Map.Entry<Integer, TextureData> startTexture = textures.firstEntry();
        return startTexture != null ? startTexture.getValue() : TextureData.EMPTY;
    }

    public @NonNull TextureData get(int ticks, int maxTicks) {
        if (maxTicks == 0) {
            return textures.firstEntry() != null ? textures.firstEntry().getValue() : TextureData.EMPTY;
        }

        return get(ticks % maxTicks);
    }

    public @NonNull TextureData get(Profile profile, EntityRenderState state) {
        return get(Math.round(state.ageInTicks), profile.maxTicks());
    }

    public Animatable set(TextureData texture, int at) {
        textures.put(at, texture);
        return this;
    }

    public boolean isEmpty() {
        return this == EMPTY || textures.isEmpty();
    }

    public void clear() {
        textures.clear();
    }

    public Map<Integer, TextureData> getTextures() {
        return textures;
    }
}
