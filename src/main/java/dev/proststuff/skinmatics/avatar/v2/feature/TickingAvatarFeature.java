package dev.proststuff.skinmatics.avatar.v2.feature;

public interface TickingAvatarFeature<S> extends AvatarFeature<S> {
    void tick(S settings);
}
