package dev.proststuff.skinmatics.api.v1.client;

import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;

public interface TrackProfileEntity {
    default void skinmatics$setTracker(ProfileTracker tracker) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default ProfileTracker skinmatics$getTracker() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default Profile skinmatics$getProfile() {
        return skinmatics$getTracker() != null ? skinmatics$getTracker().getProfile() : null;
    }
}