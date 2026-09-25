package dev.proststuff.skinmatics.avatar.v2.feature;

import dev.proststuff.skinmatics.Skinmatics;

public interface AvatarFeatures {
    BodyAvatarFeature BODY = AvatarFeatureRegistry.register(Skinmatics.of("body"), new BodyAvatarFeature());

    static void init() {}
}
