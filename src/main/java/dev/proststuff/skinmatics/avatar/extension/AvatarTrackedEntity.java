package dev.proststuff.skinmatics.avatar.extension;

import dev.proststuff.skinmatics.avatar.AvatarTracker;

public interface AvatarTrackedEntity {
    default AvatarTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented");
    }
    default void skinmatics$setTracker(AvatarTracker avatarTracker) {
        throw new UnsupportedOperationException("Unimplemented");
    }
}