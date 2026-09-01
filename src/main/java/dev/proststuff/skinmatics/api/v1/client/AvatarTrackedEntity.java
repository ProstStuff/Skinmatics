package dev.proststuff.skinmatics.api.v1.client;

import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarTracker;

public interface AvatarTrackedEntity {
    default AvatarTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}