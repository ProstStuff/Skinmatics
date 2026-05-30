package dev.proststuff.skinmatics.api.client.gui.impl;

import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public interface MouseEventListener {
    default boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    default boolean mouseMoved(double x, double y) {
        return false;
    }

    default boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        return false;
    }

    default boolean mouseReleased(@NonNull MouseButtonEvent event) {
        return false;
    }

    default boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return false;
    }
}
