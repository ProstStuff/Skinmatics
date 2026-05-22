package dev.proststuff.skinmatics.client.gui.element.panel;

import dev.proststuff.skinmatics.Skinmatics;
import net.minecraft.network.chat.Component;

public class HomePanel extends Panel {
    public HomePanel() {
        super(Skinmatics.ofSimple("home"), () -> Component.translatable("skinmatics.home", Skinmatics.getVersion()));
    }
}
