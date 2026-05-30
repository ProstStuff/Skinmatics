package dev.proststuff.skinmatics.api.client.gui.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public interface GuiRenderable {
    void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta);

    int getX();
    int getY();
    int getWidth();
    int getHeight();

    default void outline(GuiGraphicsExtractor graphics, int color) {
        graphics.outline(getX(), getY(), getWidth(), getHeight(), color);
    }
}
