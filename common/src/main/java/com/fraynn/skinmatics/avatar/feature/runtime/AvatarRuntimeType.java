package com.fraynn.skinmatics.avatar.feature.runtime;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureType;

public record AvatarRuntimeType<S, R extends AvatarRuntime<S>>(AvatarFeatureType<S> avatarFeature, AvatarRuntimeFactory<S, R> factory, boolean ticking) {

    @FunctionalInterface
    public interface AvatarRuntimeFactory<S, R extends AvatarRuntime<S>> {
        R create(AvatarTracker tracker, AvatarFeatureData<S> featureData);
    }

    public static class Builder<S, R extends AvatarRuntime<S>> {
        protected final AvatarFeatureType<S> avatarFeatureType;
        protected AvatarRuntimeFactory<S, R> factory = null;
        protected boolean ticking = false;

        public Builder(AvatarFeatureType<S> avatarFeature) {
            this.avatarFeatureType = avatarFeature;
        }

        public Builder<S, R> factory(AvatarRuntimeFactory<S, R> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<S, R> ticking() {
            this.ticking = true;
            return this;
        }

        public AvatarRuntimeType<S, R> build() {
            if (factory == null) throw new IllegalStateException("Undefined AvatarRuntimeFactory");
            return new AvatarRuntimeType<>(avatarFeatureType, factory, ticking);
        }
    }
}