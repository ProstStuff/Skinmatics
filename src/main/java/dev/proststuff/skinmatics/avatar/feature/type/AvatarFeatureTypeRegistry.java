package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class AvatarFeatureTypeRegistry {
    private static final Map<Identifier, AvatarFeatureType<?>> avatarFeatures = new HashMap<>();
    private static final Map<AvatarFeatureType<?>, Identifier> identifiers = new HashMap<>();

    public static <S> AvatarFeatureType<S> register(Identifier id, Codec<S> codec, Supplier<S> createDefault) {
        AvatarFeatureType<S> type = new AvatarFeatureType<>(id, codec, createDefault);

        if (avatarFeatures.containsKey(id)) {
            throw new IllegalStateException("Duplicate AvatarFeature with id " + id.toString());
        }

        avatarFeatures.put(id, type);
        identifiers.put(type, id);
        return type;
    }

    public static @Nullable AvatarFeatureType<?> get(Identifier id) {
        return avatarFeatures.get(id);
    }

    public static @Nullable Identifier getKey(AvatarFeatureType<?> avatarFeature) {
        return identifiers.get(avatarFeature);
    }

    public static Map<Identifier, AvatarFeatureType<?>> getAll() {
        return avatarFeatures;
    }

    private AvatarFeatureTypeRegistry() {}
}
