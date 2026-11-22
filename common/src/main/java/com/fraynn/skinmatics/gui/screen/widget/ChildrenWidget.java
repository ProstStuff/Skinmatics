package com.fraynn.skinmatics.gui.screen.widget;

import com.fraynn.utilitary.client.gui.Scrollable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChildrenWidget extends AbstractWidget implements Scrollable.Element, Scrollable.Container {
    protected ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    protected List<GuiEventListener> children = new ArrayList<>();

    protected double horizontalOffset = 0.0;
    protected double verticalOffset = 0.0;
    protected final int originX;
    protected final int originY;

    public ChildrenWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.originX = x;
        this.originY = y;
    }

    private void updateChildren() {
        ScrollDirection direction = getScrollDirection();
        int scrollX = (int) getScrollX();
        int scrollY = (int) getScrollY();

        for (GuiEventListener listener : children) {
            int sX = scrollX;
            int sY = scrollY;

            if (listener instanceof Element element && !element.canScroll(direction)) {
                sX = 0;
                sY = 0;
            }

            if (listener instanceof SimpleWidget child) {

                child.setX(getX() + child.getOriginX() - sX);
                child.setY(getY() + child.getOriginY() - sY);
            } else if (listener instanceof AbstractWidget child) {
                child.setX(getX() + child.getX() - sX);
                child.setY(getY() + child.getY() - sY);
            }
        }
    }

    public <T extends Element & Renderable & GuiEventListener> T addChild(T child) {
        children.add(child);
        updateChildren();
        return child;
    }

    public <T extends Element & Renderable & GuiEventListener> boolean removeChild(T child) {
        boolean removed = children.remove(child);
        if (removed) updateChildren();
        return removed;
    }

    @Override
    public void applyScroll(Container container) {
        Element.super.applyScroll(container);
        applyScroll();
    }

    @Override
    public void applyScroll() {
        updateChildren();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        updateChildren();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        updateChildren();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateChildren();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateChildren();
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
        return this.scrollDirection.canScrollWith(scrollDirection);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ScreenRectangle rectangle = getRectangle();
        graphics.enableScissor(rectangle.left(), rectangle.top(), rectangle.right(), rectangle.bottom());
        for (GuiEventListener child : children) if (child instanceof Renderable renderable) renderable.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

    @Override
    public double getScrollY() {
        return verticalOffset;
    }

    @Override
    public double getScrollX() {
        return horizontalOffset;
    }

    @Override
    public int getContentWidth() {
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;

        for (GuiEventListener child : children) {
            if (child instanceof Element element) {
                lowX = Math.min(lowX, element.getOriginX());
                highX = Math.max(highX, element.getOriginX() + element.getWidth());
            }
        }

        return lowX == Integer.MAX_VALUE ? 0 : highX - lowX;
    }

    @Override
    public int getContentHeight() {
        int lowY = Integer.MAX_VALUE;
        int highY = Integer.MIN_VALUE;

        for (GuiEventListener child : children) {
            if (child instanceof Element element) {
                lowY = Math.min(lowY, element.getOriginY());
                highY = Math.max(highY, element.getOriginY() + element.getHeight());
            }
        }

        return lowY == Integer.MAX_VALUE ? 0 : highY - lowY;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public void mouseMoved(double x, double y) {
        children().forEach(child -> child.mouseMoved(x, y));
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        for (GuiEventListener child : children()) {
            if (child.isMouseOver(x, y) && child.mouseScrolled(x, y, scrollX, scrollY)) return true;
        }

        horizontalOffset = evaluateScrollX(horizontalOffset - scrollX * 16.0);
        verticalOffset = evaluateScrollY(verticalOffset - scrollY * 16.0);
        applyScroll();
        return true;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        for (GuiEventListener child : children()) {
            if (child.isMouseOver(event.x(), event.y()) && child.mouseReleased(event)) return true;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        for (GuiEventListener child : children()) {
            if (child.isMouseOver(event.x(), event.y()) && child.mouseDragged(event, dx, dy)) return true;
        }

        horizontalOffset = evaluateScrollX(horizontalOffset - dx);
        verticalOffset = evaluateScrollY(verticalOffset - dy);
        applyScroll();
        return true;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (GuiEventListener child : children()) {
            if (child.isMouseOver(event.x(), event.y()) && child.mouseClicked(event, doubleClick)) return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        for (GuiEventListener child : children()) {
            if (child.keyPressed(event)) return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        for (GuiEventListener child : children()) {
            if (child.keyReleased(event)) return true;
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        for (GuiEventListener child : children()) {
            if (child.charTyped(event)) return true;
        }

        return super.charTyped(event);
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {}
}