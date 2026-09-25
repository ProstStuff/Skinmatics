package dev.proststuff.skinmatics.entity;

import dev.proststuff.skinmatics.avatar.v1.AvatarTracker;

public interface SkinmaticsAvatarRenderState extends AvatarTrackedEntity {
    default boolean skinmatics$eyesClosed() {
        throw new IllegalStateException("Unimplemented");
    }

    default void skinmatics$apply(AvatarTracker tracker, boolean eyesClosed) {
        throw new IllegalStateException("Unimplemented");
    }
}
