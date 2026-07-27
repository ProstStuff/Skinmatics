package dev.proststuff.skinmatics.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class TextButton extends SimpleBackgroundButton<TextButton> {
    protected final Supplier<Component> textBuilder;
    protected int spacing = 4;

    public TextButton(int x, int y, int width, int height, Supplier<Component> textBuilder, OnPress<TextButton> onPress) {
        super(x, y, width, height, Component.empty(), onPress);
        this.textBuilder = textBuilder;
    }

    public TextButton(int x, int y, int width, int height, Component message, OnPress<TextButton> onPress) {
        this(x, y, width, height, () -> message, onPress);
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public int getWidth() {
        return Math.max(super.getWidth(), font().width(getMessage()) + spacing * 2);
    }

    @Override
    public @NonNull Component getMessage() {
        return textBuilder.get();
    }

    @Override
    public void renderContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Component text = getMessage();
        int x = getX();
        int width = getWidth();
        graphics.centeredText(font(), text, x + width / 2, getY() + getHeight() / 2 - font().lineHeight / 2, getContentColor());
    }
}
