package dev.proststuff.skinmatics.api.v1.client;

import dev.proststuff.skinmatics.client.avatar.AvatarTracker;

public interface SkinmaticsAvatarRenderState extends AvatarTrackedEntity {
    default boolean skinmatics$eyesClosed() {
        throw new IllegalStateException("Unimplemented");
    }

    default void skinmatics$apply(AvatarTracker tracker, boolean eyesClosed) {
        throw new IllegalStateException("Unimplemented");
    }
}
