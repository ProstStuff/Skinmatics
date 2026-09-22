package dev.proststuff.skinmatics.api.v1.client;

import dev.proststuff.skinmatics.client.avatar.AvatarTracker;

public interface AvatarTrackedEntity {
    default AvatarTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}