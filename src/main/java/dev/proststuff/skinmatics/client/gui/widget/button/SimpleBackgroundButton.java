package dev.proststuff.skinmatics.client.gui.widget.button;

import dev.proststuff.skinmatics.client.gui.SkinmaticsUIElement;
import dev.proststuff.utilitary.api.v1.client.gui.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@SuppressWarnings("unchecked")
public abstract class SimpleBackgroundButton<T extends SimpleBackgroundButton<T>> extends AbstractButton implements SkinmaticsUIElement, Scrollable.Element, Tickable, GroupableElement, ScreenAutoParentable {
    protected final OnPress<T> onPress;
    protected final int originX;
    protected final int originY;

    protected boolean overrideFocus = false;
    protected boolean focusState = false;
    protected boolean renderBackground = true;
    protected ScrollDirection scrollDirection = ScrollDirection.VERTICAL;
    protected Screen parent;
    protected Identifier group = MultiFocusScreen.DEFAULT_GROUP;

    public SimpleBackgroundButton(int x, int y, int width, int height, Component message, OnPress<T> onPress) {
        super(x, y, width, height, message);
        this.originX = x;
        this.originY = y;
        this.onPress = onPress;

        setTooltipDelay(Duration.ofSeconds(1));
    }

    public void setGroup(Identifier group) {
        this.group = group;
    }

    @Override
    public Identifier getGroup() {
        return group;
    }

    @Override
    public void setParent(Screen parent) {
        this.parent = parent;
    }

    @Override
    public @Nullable Screen getParent() {
        return parent;
    }

    public void setOverrideFocus(boolean overrideFocus, boolean focused) {
        this.overrideFocus = overrideFocus;
        this.focusState = focused;
    }

    @Override
    public boolean isFocused() {
        return overrideFocus ? focusState : super.isFocused();
    }

    public T ofTooltip(Tooltip tooltip) {
        setTooltip(tooltip);
        return (T) this;
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

    public int getBackgroundColor() {
        return backgroundColor();
    }

    public int getContentColor() {
        return active ? foregroundColor() : foregroundColor(0.5F);
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void renderBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x0 = getX();
        int y0 = getY();
        int x1 = getX() + getWidth();
        int y1 = getY() + getHeight();
        int color = 0;

        if (active && isHoveredOrFocused()) {
            if (isFocused()) {
                color = accentColor();
            } else {
                color = foregroundColor(0.5F);
            }
        } else if (renderBackground) {
            color = getBackgroundColor();
        }

        graphics.fill(x0, y0, x1, y1, color);
    }

    public abstract void renderContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

    @Override
    public int getOriginX() {
        return originX;
    }

    @Override
    public int getOriginY() {
        return originY;
    }

    @Override
    protected final void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        renderBackground(graphics, mouseX, mouseY, a);
        renderContent(graphics, mouseX, mouseY, a);
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundManager) {
        //soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, SkinmaticsClient.getInstance().between(0.8F, 1.2F)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        onPress.onPress((T) this, input);
    }

    @Override
    protected final void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick() {}

    public interface OnPress<T> {
        void onPress(T button, InputWithModifiers input);
    }
}
