package dev.proststuff.skinmatics.avatar.v2.feature;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class AvatarFeatureRegistry {
    private static final Map<Identifier, AvatarFeature<?>> avatarFeatures = new HashMap<>();

    public static @Nullable AvatarFeature<?> get(Identifier id) {
        return avatarFeatures.get(id);
    }

    public static <A extends AvatarFeature<?>> A register(Identifier id, A avatarFeature) {
        if (avatarFeatures.put(id, avatarFeature) != null) {
            throw new IllegalStateException("Duplicate avatar feature: " + id);
        }

        return avatarFeature;
    }

    private AvatarFeatureRegistry() {}
}
