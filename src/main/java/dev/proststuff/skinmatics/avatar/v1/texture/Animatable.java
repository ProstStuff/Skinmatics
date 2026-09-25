package dev.proststuff.skinmatics.avatar.v1.texture;

import com.mojang.serialization.Codec;
import dev.proststuff.skinmatics.avatar.v1.AvatarTracker;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class Animatable {
    public static final Codec<Animatable> CODEC = Codec.unboundedMap(AvatarTextureData.CODEC, Codec.INT.listOf()).xmap(
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
                Map<AvatarTextureData, List<Integer>> map = new LinkedHashMap<>();
                animatable.getTextures().forEach((frame, texture) -> map.computeIfAbsent(texture, t -> new ArrayList<>()).add(frame));
                return map;
            }
    );
    public static final Animatable EMPTY = empty();

    public static Animatable empty() {
        return new Animatable();
    }

    public static Animatable of(Identifier texture) {
        Animatable animatable = new Animatable();
        animatable.set(new AvatarTextureData.Reference(texture), 0);
        return animatable;
    }

    protected final NavigableMap<Integer, AvatarTextureData> textures;

    public Animatable() {
        textures = new TreeMap<>();
    }

    public @NonNull AvatarTextureData get(int at) {
        if (textures.containsKey(at)) return textures.get(at);

        Map.Entry<Integer, AvatarTextureData> nextTexture = textures.higherEntry(at);
        if (nextTexture != null) return nextTexture.getValue();
        Map.Entry<Integer, AvatarTextureData> startTexture = textures.firstEntry();
        return startTexture != null ? startTexture.getValue() : AvatarTextureData.EMPTY;
    }

    public @NonNull AvatarTextureData get(int ticks, int maxTicks) {
        if (maxTicks == 0) {
            return textures.firstEntry() != null ? textures.firstEntry().getValue() : AvatarTextureData.EMPTY;
        }

        return get(ticks % maxTicks);
    }

    public @NonNull AvatarTextureData get(AvatarTracker tracker) {
        return get(tracker.tickCount, tracker.getAvatar().maxTicks);
    }

    public Animatable set(AvatarTextureData texture, int at) {
        textures.put(at, texture);
        return this;
    }

    public boolean isEmpty() {
        return this == EMPTY || textures.isEmpty();
    }

    public void clear() {
        textures.clear();
    }

    public Map<Integer, AvatarTextureData> getTextures() {
        return textures;
    }
}
