package dev.proststuff.skinmatics.api.client.impl.mixin;

import java.util.UUID;

public interface UUIDHolder {
    default UUID skinmatics$getUUID() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default void skinmatics$setUUID(UUID uuid) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
}
