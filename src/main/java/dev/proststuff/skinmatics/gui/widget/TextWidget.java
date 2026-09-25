package dev.proststuff.skinmatics.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TextWidget extends SimpleWidget {
    protected int textInset = 4;
    protected int textColor = foregroundColor();

    public boolean visualizeActivity = true;

    public TextWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        updateHeight(Minecraft.getInstance().font);
    }

    public void setTextInset(int textInset) {
        this.textInset = textInset;
        updateHeight(Minecraft.getInstance().font);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void updateHeight(Font font) {
        int textWidth = getWidth() - textInset * 2;
        List<FormattedCharSequence> lines = font.split(getMessage(), textWidth);
        int lineHeight = font.lineHeight;
        int newHeight = lines.size() * lineHeight + textInset * 2;
        setHeight(newHeight);
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Font font = Minecraft.getInstance().font;
        int textWidth = getWidth() - textInset * 2;
        List<FormattedCharSequence> lines = font.split(getMessage(), textWidth);
        int y = getY() + textInset;
        int color = visualizeActivity ? ARGB.color(active ? 1.0F : 0.5F, textColor) : textColor;

        for (FormattedCharSequence line : lines) {
            graphics.text(font, line, getX() + textInset, y, color);
            y += font.lineHeight;
        }
    }
}
