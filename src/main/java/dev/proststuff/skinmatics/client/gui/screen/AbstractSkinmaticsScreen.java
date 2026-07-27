package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.client.gui.SkinmaticsUIElement;
import dev.proststuff.utilitary.api.v1.client.gui.MultiFocusScreen;
import dev.proststuff.utilitary.api.v1.client.gui.Parentable;
import dev.proststuff.utilitary.api.v1.client.gui.Scrollable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSkinmaticsScreen extends MultiFocusScreen implements SkinmaticsUIElement, Parentable<Screen>, Scrollable.Container {
    public static final double SCROLL_SPEED = 16.0;
    public static final int INSET = 16;

    protected final Screen parent;
    protected ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    protected double offsetX = 0.0;
    protected double offsetY = 0.0;

    public AbstractSkinmaticsScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public @Nullable Screen getParent() {
        return parent;
    }

    @Override
    public int getScrollableWidth() {
        return width;
    }

    @Override
    public int getScrollableHeight() {
        return height;
    }

    @Override
    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }

    @Override
    public boolean canScroll(ScrollDirection scrollDirection) {
        return true;
    }

    @Override
    public double getScrollX() {
        return offsetX;
    }

    @Override
    public double getScrollY() {
        return offsetY;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        offsetX = evaluateScrollX(offsetX - scrollX * SCROLL_SPEED);
        offsetY = evaluateScrollY(offsetY - scrollY * SCROLL_SPEED);
        applyScroll();
        return true;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (!super.mouseDragged(event, dx, dy)) {
            offsetX = evaluateScrollX(offsetX - dx);
            offsetY = evaluateScrollY(offsetY - dy);

            applyScroll();
        }

        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}