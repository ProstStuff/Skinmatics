package dev.proststuff.skinmatics.client.gui.widget;

import dev.proststuff.utilitary.api.v1.client.gui.Scrollable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ScrollableEditBox extends EditBox implements Scrollable.Element {
    protected final int originX;
    protected final int originY;
    protected ScrollDirection scrollDirection;

    public ScrollableEditBox(final Font font, final int x, final int y, final int width, final int height, @Nullable final EditBox oldBox, final Component narration) {
        super(font, x, y, width, height, oldBox, narration);
        this.originX = x;
        this.originY = y;
        this.scrollDirection = ScrollDirection.VERTICAL;
    }

    public ScrollableEditBox(final Font font, final int width, final int height, final Component narration) {
        this(font, 0, 0, width, height, narration);
    }

    public ScrollableEditBox(final Font font, final int x, final int y, final int width, final int height, final Component narration) {
        this(font, x, y, width, height, null, narration);
    }

    public void setScrollDirection(ScrollDirection scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    @Override
    public int getOriginX() {
        return originX;
    }

    @Override
    public int getOriginY() {
        return originY;
    }

    @Override
    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }

    @Override
    public boolean canScroll(ScrollDirection scrollDirection) {
        return getScrollDirection().canScrollWith(scrollDirection);
    }
}
