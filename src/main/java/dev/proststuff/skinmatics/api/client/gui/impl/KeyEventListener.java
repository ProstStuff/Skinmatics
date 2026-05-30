package dev.proststuff.skinmatics.api.client.gui.impl;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.NonNull;

public interface KeyEventListener {
    default boolean keyPressed(@NonNull KeyEvent event) {
        return false;
    }


    default boolean keyReleased(@NonNull KeyEvent event) {
        return false;
    }

    default boolean charTyped(@NonNull CharacterEvent event) {
        return false;
    }
}
