package com.fraynn.skinmatics.gui.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class TextButton extends ClickableButton {
    public TextButton(int x, int y, int width, int height, Component message, OnClick onClick) {
        super(x, y, width, height, message, onClick);
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Font font = minecraft().font;
        graphics.centeredText(font, message, getX() + getWidth() / 2, getY() + getHeight() / 2 - font.lineHeight / 2, ARGB.color(active ? 1.0F : 0.5F, foregroundColor()));
    }
}
