package com.fraynn.skinmatics.gui.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class OptionWheelScreen extends SkinmaticsScreen {
    public static final Component TITLE = Component.translatable("skinmatics.wheel");

    public OptionWheelScreen() {
        super(TITLE, null);
    }

    @Override
    public boolean showCloseButton() {
        return false;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void mouseMoved(double x, double y) {}

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        /*if (SkinmaticsKeyMappings.OPEN_WHEELS.get().matches(event)) {
            onClose();
            return true;
        }*/

        return super.keyReleased(event);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}