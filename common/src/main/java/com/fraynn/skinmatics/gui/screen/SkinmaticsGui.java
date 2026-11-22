package com.fraynn.skinmatics.gui.screen;

import com.fraynn.skinmatics.config.SkinmaticsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public interface SkinmaticsGui {
    default SkinmaticsConfig config() {
        return SkinmaticsConfig.getInstance();
    }

    default Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    default int backgroundColor(float alpha) {
        return config().getGui().getBackground(alpha);
    }

    default int backgroundColor() {
        return config().getGui().getBackground(0.5F);
    }

    default int foregroundColor(float alpha) {
        return config().getGui().getForeground(alpha);
    }

    default int foregroundColor() {
        return config().getGui().getForeground(1.0F);
    }

    default int accentColor(float alpha) {
        return config().getGui().getAccent(alpha);
    }

    default int accentColor() {
        return config().getGui().getAccent(1.0F);
    }

    static Screen openScreen(Screen parent) {
        return new AvatarScreen(parent);
    }
}
