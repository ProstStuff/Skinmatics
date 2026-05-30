package dev.proststuff.skinmatics.api.client.impl.mixin;

public interface UIRendered {
    default boolean skinmatics$isUiRendered() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default void skinmatics$uiRender() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
}
