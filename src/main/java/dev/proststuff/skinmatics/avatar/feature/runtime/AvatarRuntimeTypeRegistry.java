package dev.proststuff.skinmatics.avatar.feature.runtime;

import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AvatarRuntimeTypeRegistry {
    private static final Map<Identifier, AvatarRuntimeType<?, ?>> avatarRuntimes = new HashMap<>();
    private static final Map<AvatarRuntimeType<?, ?>, Identifier> identifiers = new HashMap<>();

    public static <S, R extends AvatarRuntime<S>> AvatarRuntimeType<S, R> register(AvatarRuntimeType<S, R> avatarRuntimeType) {
        Identifier id = avatarRuntimeType.avatarFeature().id();

        if (avatarRuntimes.containsKey(id)) {
            throw new IllegalStateException("Duplicate AvatarFeature with id " + id.toString());
        }

        avatarRuntimes.put(id, avatarRuntimeType);
        identifiers.put(avatarRuntimeType, id);
        return avatarRuntimeType;
    }

    public static <S, R extends AvatarRuntime<S>> AvatarRuntimeType<S, R> register(AvatarFeatureType<S> avatarFeature, Consumer<AvatarRuntimeType.Builder<S, R>> builderFactory) {
        AvatarRuntimeType.Builder<S, R> builder = new AvatarRuntimeType.Builder<>(avatarFeature);
        builderFactory.accept(builder);
        return register(builder.build());
    }

    public static @Nullable AvatarRuntimeType<?, ?> get(Identifier id) {
        return avatarRuntimes.get(id);
    }

    public static @Nullable Identifier getKey(AvatarRuntimeType<?, ?> avatarFeature) {
        return identifiers.get(avatarFeature);
    }

    private AvatarRuntimeTypeRegistry() {}
}
