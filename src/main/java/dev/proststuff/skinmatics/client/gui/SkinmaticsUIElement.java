package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.client.skinmatics.config.GUI;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public interface SkinmaticsUIElement {
    int INSET = 16;
    int SPACING = 4;

    default Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    default Font font() {
        return minecraft().font;
    }

    default SkinmaticsConfig config() {
        return SkinmaticsConfig.getInstance();
    }

    default GUI gui() {
        return config().getGui();
    }

    default int backgroundColor(float alpha) {
        return gui().getBackgroundColor().get(alpha);
    }

    default int backgroundColor() {
        return backgroundColor(0.5F);
    }

    default int foregroundColor(float alpha) {
        return gui().getForegroundColor().get(alpha);
    }

    default int foregroundColor() {
        return foregroundColor(1.0F);
    }

    default int accentColor(float alpha) {
        return gui().getAccentColor().get(alpha);
    }

    default int accentColor() {
        return accentColor(1.0F);
    }
}
