package dev.proststuff.skinmatics.client.gui.impl;

public interface ScreenEventListener {
    void init(int width, int height);
    void setPosition(int x, int y);

    default boolean onClose() {
        return false;
    }
}
