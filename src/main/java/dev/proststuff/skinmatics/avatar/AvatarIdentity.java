package dev.proststuff.skinmatics.avatar;

import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;
import org.jspecify.annotations.Nullable;

public interface AvatarIdentity {
    <S> @Nullable AvatarFeatureData<S> getFeature(AvatarFeatureType<S> avatarFeature);
}
