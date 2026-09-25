package dev.proststuff.skinmatics.gui.widget;

import dev.proststuff.skinmatics.gui.SkinmaticsGui;
import dev.proststuff.utilitary.api.v1.client.Tickable;
import dev.proststuff.utilitary.api.v1.client.gui.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class SimpleWidget extends AbstractWidget implements SkinmaticsGui, Scrollable.Element, GroupableElement, ScreenAutoParentable, Focusable, Tickable {
    protected int originX;
    protected int originY;
    protected Identifier group = MultiFocusScreen.DEFAULT_GROUP;
    protected ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    protected Screen parent;

    public boolean showBackground = true;
    public boolean focusable = true;
    public boolean hoverable = true;

    public SimpleWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.originX = x;
        this.originY = y;
    }

    @Override
    public boolean isHovered() {
        return hoverable && super.isHovered();
    }

    public int getFocusColor() {
        return accentColor();
    }

    public int getHoverColor() {
        return foregroundColor(0.5F);
    }

    public int getIdleColor() {
        return showBackground ? backgroundColor() : 0;
    }

    protected void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (isFocused()) graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getFocusColor());
        else if (isHovered() && active) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getHoverColor());
        } else if (showBackground) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), getIdleColor());
        }
    }

    protected abstract void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

    @Override
    public boolean shouldTick() {
        return false;
    }

    @Override
    public void tick() {}

    @Override
    public boolean isFocusable() {
        return focusable;
    }

    @Override
    public void setFocused(boolean focused) {
        if (focusable) super.setFocused(focused);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractBackground(graphics, mouseX, mouseY, a);
        extractContent(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

    @Override
    public Identifier getGroup() {
        return group;
    }

    public void setGroup(Identifier group) {
        this.group = group;
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
    public @Nullable Screen getParent() {
        return parent;
    }

    @Override
    public void setParent(Screen parent) {
        this.parent = parent;
    }

    public void setScrollDirection(ScrollDirection scrollDirection) {
        this.scrollDirection = scrollDirection;
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
    public void playDownSound(@NonNull SoundManager soundManager) {}
}