package dev.proststuff.skinmatics.api.client.impl.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public interface GuiRenderable {
    void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta);
}
