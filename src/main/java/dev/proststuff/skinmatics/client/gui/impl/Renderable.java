package dev.proststuff.skinmatics.client.gui.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface Renderable {
    void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY);
}
