package dev.proststuff.skinmatics.avatar.feature;

import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;

import java.util.Optional;

public record AvatarFeatureData<S>(AvatarFeatureType<S> feature, S settings) {
    public AvatarFeatureData(AvatarFeatureType<S> feature, Optional<Object> optional) {
        this(feature, (S) optional.orElseGet(() -> feature.createDefault().get()));
    }
}