package dev.proststuff.skinmatics.api.client.gui.impl;

public interface ScreenEventListener {
    void init(int width, int height);
    void setPosition(int x, int y);

    default void onClose() {
    }
}
