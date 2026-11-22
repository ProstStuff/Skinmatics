package com.fraynn.skinmatics.avatar.feature.runtime.light;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class SimpleDynamicLight extends DynamicLight<Integer> {
    public static final Codec<Integer> CODEC = Codec.INT;

    public SimpleDynamicLight(LightAvatarRuntime avatarRuntime) {
        super(avatarRuntime);
    }

    @Override
    public void tick() {}

    @Override
    public @NonNull Codec<Integer> getCodec() {
        return CODEC;
    }

    @Override
    public @Range(from = 0, to = 15) int get() {
        return (int) avatarRuntime.getData().settings().settings();
    }
}
