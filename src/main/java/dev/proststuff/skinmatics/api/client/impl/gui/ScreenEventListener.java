package dev.proststuff.skinmatics.api.client.impl.gui;

public interface ScreenEventListener {
    void init(int width, int height);
    void setPosition(int x, int y);

    default boolean onClose() {
        return false;
    }
}
