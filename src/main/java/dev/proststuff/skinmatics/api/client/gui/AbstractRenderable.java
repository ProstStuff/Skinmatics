package dev.proststuff.skinmatics.api.client.gui;

import dev.proststuff.skinmatics.api.client.gui.impl.GuiRenderable;
import dev.proststuff.skinmatics.api.client.gui.impl.ScreenEventListener;

public abstract class AbstractRenderable implements GuiRenderable, ScreenEventListener {
    protected int x = 0;
    protected int y = 0;
    protected int screenWidth = 0;
    protected int screenHeight = 0;

    public AbstractRenderable() {}

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean in(double pointX, double pointY) {
        return pointX >= getX() && pointX < getX() + getWidth() && pointY >= getY() && pointY < getY() + getHeight();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public abstract int getWidth();
    public abstract int getHeight();
}
