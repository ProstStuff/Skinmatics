package dev.proststuff.skinmatics.entity;

import dev.proststuff.skinmatics.avatar.v1.AvatarTracker;

public interface AvatarTrackedEntity {
    default AvatarTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}