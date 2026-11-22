package com.fraynn.skinmatics.avatar.feature.runtime.light;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DynamicLightFactoryRegistry {
    private static final Map<Identifier, DynamicLightFactory<?>> DYNAMIC_LIGHTS = new HashMap<>();
    private static final Map<DynamicLightFactory<?>, Codec<?>> DL_CODECS = new HashMap<>();

    public static <S, F extends DynamicLightFactory<S>> F register(Identifier id, F factory, Codec<S> settingsCodec) {
        if (DYNAMIC_LIGHTS.containsKey(id)) {
            throw new IllegalStateException("Duplicate DynamicLight with id " + id.toString());
        }

        DYNAMIC_LIGHTS.put(id, factory);
        DL_CODECS.put(factory, settingsCodec);
        return factory;
    }

    public static @Nullable DynamicLightFactory<?> get(Identifier id) {
        return DYNAMIC_LIGHTS.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <S> @Nullable Codec<S> getCodec(DynamicLightFactory<S> factory) {
        return (Codec<S>) DL_CODECS.get(factory);
    }

    @FunctionalInterface
    public interface DynamicLightFactory<S> {
        DynamicLight<S> create(LightAvatarRuntime avatarRuntime);
    }
}