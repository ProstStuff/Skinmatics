package com.fraynn.skinmatics.avatar.feature.runtime.light;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public abstract class DynamicLight<S> {
    protected final LightAvatarRuntime avatarRuntime;

    public DynamicLight(LightAvatarRuntime avatarRuntime) {
        this.avatarRuntime = avatarRuntime;
    }

    public abstract @Range(from = 0, to = 15) int get();
    public abstract void tick();

    public abstract @NonNull Codec<S> getCodec();
}