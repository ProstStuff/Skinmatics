package dev.proststuff.skinmatics.client.gui.widget;

import dev.proststuff.skinmatics.client.gui.SkinmaticsUIElement;
import dev.proststuff.utilitary.api.v1.client.gui.Scrollable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unchecked")
public abstract class ScrollableWidget<T extends ScrollableWidget<T>> extends AbstractWidget implements Scrollable.Element, SkinmaticsUIElement {
    protected final int originX;
    protected final int originY;
    protected ScrollDirection scrollDirection = ScrollDirection.VERTICAL;

    public ScrollableWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.originX = x;
        this.originY = y;
    }

    public T setScrollDirection(ScrollDirection scrollDirection) {
        this.scrollDirection = scrollDirection;
        return (T) this;
    }

    @Override
    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }

    @Override
    public boolean canScroll(ScrollDirection scrollDirection) {
        return getScrollDirection().canScrollWith(scrollDirection);
    }

    @Override
    public int getOriginX() {
        return originX;
    }

    @Override
    public int getOriginY() {
        return originY;
    }
}
