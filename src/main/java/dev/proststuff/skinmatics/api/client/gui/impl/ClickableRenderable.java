package dev.proststuff.skinmatics.api.client.gui.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ClickableRenderable extends MouseEventListener, KeyEventListener {
    Map<String, ClickableRenderable> HOVERED = new LinkedHashMap<>();
    Map<String, ClickableRenderable> FOCUSED = new LinkedHashMap<>();

    default void unhover() {
        HOVERED.values().removeIf(this::equals);
    }

    default void unfocus() {
        FOCUSED.values().removeIf(this::equals);
    }

    default void hover() {
        HOVERED.put(getCategory(), this);
    }

    default void focus() {
        FOCUSED.put(getCategory(), this);
    }

    default boolean isHoverable() {
        return true;
    }

    default boolean isFocusable() {
        return true;
    }

    default boolean isFocused() {
        return isFocusable() && FOCUSED.get(getCategory()) == this;
    }

    default boolean isHovered() {
        return isHoverable() && HOVERED.get(getCategory()) == this;
    }

    @Override
    default boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (isHovered()) {
            if (isFocusable() && isPressing(event.button(), event.x(), event.y())) {
                focus();
                return true;
            } else {
                unfocus();
            }
        }

        return false;
    }

    @Override
    default boolean mouseMoved(double x, double y) {
        if (in(x, y)) {
            if (isHoverable()) {
                hover();
            }
            return true;
        } else {
            unhover();
        }

        return false;
    }

    default void renderClickableState(GuiGraphicsExtractor graphics, int focusedColor, int hoveredColor) {
        if (isHovered() || isFocused()) {
            int x = getX();
            int y = getY();
            graphics.fill(x, y, x + getWidth(), y + getHeight(), isFocused() ? focusedColor : hoveredColor);
        }
    }

    default boolean isPressing(int button, double mouseX, double mouseY) {
        return press(button, mouseX, mouseY);
    }

    int getX();
    int getY();
    int getWidth();
    int getHeight();

    boolean press(int button, double mouseX, double mouseY);
    boolean in(double mouseX, double mouseY);
    String getCategory();
}
