package com.fraynn.skinmatics.avatar.extension;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import org.jspecify.annotations.Nullable;

public interface AvatarTrackedEntity {
    default @Nullable AvatarTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented");
    }

    default void skinmatics$setTracker(AvatarTracker avatarTracker) {
        throw new UnsupportedOperationException("Unimplemented");
    }
}