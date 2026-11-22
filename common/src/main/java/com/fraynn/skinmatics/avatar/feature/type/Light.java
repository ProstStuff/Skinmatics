package com.fraynn.skinmatics.avatar.feature.type;

import com.fraynn.skinmatics.avatar.feature.runtime.light.DynamicLightFactoryRegistry;
import com.fraynn.skinmatics.avatar.feature.runtime.light.LightAvatarRuntime;
import com.fraynn.utilitary.util.Tried;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public record Light<S>(Identifier type, S settings) {
    public static final Codec<Light<?>> CODEC = Identifier.CODEC
            .validate(id -> DynamicLightFactoryRegistry.get(id) != null ? DataResult.success(id) : DataResult.error(() -> "Unknown dynamic light type: " + id))
            .dispatch("type", Light::type, Light::codecFor);

    private static MapCodec<? extends Light<?>> codecFor(Identifier id) {
        return codecFor(id, Objects.requireNonNull(DynamicLightFactoryRegistry.get(id)));
    }

    private static <S> MapCodec<Light<S>> codecFor(Identifier id, DynamicLightFactoryRegistry.DynamicLightFactory<S> light) {
        return Objects.requireNonNull(DynamicLightFactoryRegistry.getCodec(light)).fieldOf("settings").xmap(
                settings -> new Light<>(id, settings),
                Light::settings
        );
    }

    public Tried<DynamicLightFactoryRegistry.DynamicLightFactory<?>> getFactory(LightAvatarRuntime avatarRuntime) {
        return Tried.tryCatch(() -> DynamicLightFactoryRegistry.get(type));
    }
}