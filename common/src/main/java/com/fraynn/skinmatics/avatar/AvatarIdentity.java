package com.fraynn.skinmatics.avatar;

import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureType;
import org.jspecify.annotations.Nullable;

public interface AvatarIdentity {
    <S> @Nullable AvatarFeatureData<S> getFeature(AvatarFeatureType<S> avatarFeature);
    boolean isHidden(AvatarFeatureType<?> avatarFeature);
}
