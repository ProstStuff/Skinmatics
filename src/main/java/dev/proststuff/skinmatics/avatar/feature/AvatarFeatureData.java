package dev.proststuff.skinmatics.avatar.feature;

import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;

public record AvatarFeatureData<S>(AvatarFeatureType<S> feature, S settings) {}