package dev.proststuff.skinmatics.client.gui.impl;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public interface ScreenEventListener {
    default void mouseMoved(double x, double y) {}

    default boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    default boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        return false;
    }

    default boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return false;
    }

    default boolean mouseReleased(@NonNull MouseButtonEvent event) {
        return false;
    }

    default boolean keyPressed(KeyEvent event) {
        return false;
    }

    default boolean keyReleased(KeyEvent event) {
        return false;
    }


    default boolean charTyped(CharacterEvent event) {
        return false;
    }
}
