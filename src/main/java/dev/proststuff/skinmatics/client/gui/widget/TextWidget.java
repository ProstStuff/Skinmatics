package dev.proststuff.skinmatics.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class TextWidget extends ScrollableWidget<TextWidget> {
    protected final TextAlignment horizontalAlignment;
    protected final TextAlignment verticalAlignment;

    public TextWidget(int x, int y, int width, int height, Component message, TextAlignment horizontalAlignment, TextAlignment verticalAlignment) {
        super(x, y, width, height, message);
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), font().wordWrapHeight(message, width));
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Font font = font();
        Component message = getMessage();
        int width = getWidth() - SPACING * 2;

        graphics.textWithWordWrap(font(), message,
                SPACING + horizontalAlignment.calculateLeft(getX(), font, message.getVisualOrderText()),
                verticalAlignment.calculateLeft(getY() + getHeight() / 2, font.wordWrapHeight(message, width)),
                width, foregroundColor()
        );
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {

    }
}
