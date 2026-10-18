package dev.proststuff.skinmatics.avatar.feature.runtime;

import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;

public record AvatarRuntimeType<S, R extends AvatarRuntime<S>>(AvatarFeatureType<S> avatarFeature, AvatarRuntimeFactory<S, R> factory, boolean ticking) {

    @FunctionalInterface
    public interface AvatarRuntimeFactory<S, R extends AvatarRuntime<S>> {
        R create(AvatarTracker tracker, AvatarFeatureData<S> featureData);
    }

    public static class Builder<S, R extends AvatarRuntime<S>> {
        protected AvatarRuntimeFactory<S, R> factory = null;
        protected boolean ticking = false;

        public Builder() {}

        public Builder<S, R> factory(AvatarRuntimeFactory<S, R> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<S, R> ticking() {
            this.ticking = true;
            return this;
        }

        public AvatarRuntimeType<S, R> build(AvatarFeatureType<S> avatarFeature) {
            if (factory == null) throw new IllegalStateException("Undefined AvatarRuntimeFactory");
            return new AvatarRuntimeType<>(avatarFeature, factory, ticking);
        }
    }
}