package dev.proststuff.skinmatics.api.client.impl.mixin;

public interface EntityPreviewer {
    default void skinmatics$preview() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default boolean skinmatics$isPreviewing() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
}
