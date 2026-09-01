package dev.proststuff.skinmatics.api.v1.client;

import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarTracker;

public interface SkinmaticsAvatarRenderState extends AvatarTrackedEntity {
    default boolean skinmatics$isDead() {
        throw new IllegalStateException("Unimplemented");
    }

    default boolean skinmatics$eyesClosed() {
        throw new IllegalStateException("Unimplemented");
    }

    default void skinmatics$apply(AvatarTracker tracker, boolean dead, boolean eyesClosed) {
        throw new IllegalStateException("Unimplemented");
    }
}
