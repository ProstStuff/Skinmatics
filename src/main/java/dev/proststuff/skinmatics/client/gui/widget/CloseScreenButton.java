package dev.proststuff.skinmatics.client.gui.widget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class CloseScreenButton extends TextButton {
    public CloseScreenButton(int x, int y) {
        super(x, y, 16, 15, Component.literal("X"), button -> Optional.ofNullable(button.getParent()).ifPresent(Screen::onClose));
    }
}
