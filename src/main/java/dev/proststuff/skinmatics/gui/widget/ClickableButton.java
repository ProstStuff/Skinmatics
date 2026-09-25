package dev.proststuff.skinmatics.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class ClickableButton extends SimpleWidget {
    protected final OnClick onClick;
    protected boolean holdToTrigger = false;
    protected boolean held = false;
    protected int triggerTickCount = 20;
    protected int tickCount = 0;
    protected int direction = 0;

    public ClickableButton(int x, int y, int width, int height, Component message, OnClick onClick) {
        super(x, y, width, height, message);
        this.onClick = onClick;
    }

    public void setHolding(int triggerTickCount) {
        this.holdToTrigger = triggerTickCount > 0;
        this.triggerTickCount = triggerTickCount;
    }

    public void setDirection(int direction) {
        this.direction = direction % 4;
    }

    @Override
    public int getFocusColor() {
        return holdToTrigger ? (isHovered ? getHoverColor() : getIdleColor()) : super.getFocusColor();
    }

    @Override
    protected void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        if (!holdToTrigger) return;
        float alp = (tickCount / (float) triggerTickCount);
        int color = accentColor();

        if (direction == 0) {
            graphics.fill(getX(), getY(), getX() + (int) (getWidth() * alp), getY() + getHeight(), color);
        } else if (direction == 1) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + (int) (getHeight() * alp), color);
        } else if (direction == 2) {
            graphics.fill(getX() + (int) (getWidth() - (getWidth() * alp)), getY(), getX() + getWidth(), getY() + getHeight(), color);
        } else if (direction == 3) {
            graphics.fill(getX(), getY() + (int) (getHeight() - (getHeight() * alp)), getX() + getWidth(), getY() + getHeight(), color);
        }
    }

    @Override
    public boolean shouldTick() {
        return holdToTrigger;
    }

    @Override
    public void tick() {
        if (held) {
            if (tickCount++ == triggerTickCount) {
                held = false;
                tickCount = 0;
                onClick.onClick(this);
            }
        } else {
            tickCount = Math.max(tickCount - 2, 0);
        }
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        onClick.onClick(this);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (holdToTrigger) {
            held = true;
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (held) {
            held = false;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!isActive()) {
            return false;
        } else if (event.isSelection()) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onClick.onClick(this);
            return true;
        } else {
            return false;
        }
    }

    public interface OnClick {
        void onClick(ClickableButton widget);
    }
}