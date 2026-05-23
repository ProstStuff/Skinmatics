package dev.proststuff.skinmatics.client.gui.element.panel.home;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.element.panel.button.ColorPanelButton;
import dev.proststuff.skinmatics.client.gui.element.panel.button.IntegerProfilePanelButton;
import net.minecraft.network.chat.Component;

public class HomePanel extends Panel {
    public HomePanel() {
        super(Skinmatics.ofSimple("home"), () -> Component.translatable("skinmatics.home", Skinmatics.getVersion()));
        addButton(new IntegerProfilePanelButton(Skinmatics.ofSimple("update_range"), SkinmaticsConfig.INSTANCE.updateRange));
        addButton(new ColorPanelButton(Skinmatics.ofSimple("background_color"), SkinmaticsConfig.INSTANCE.backgroundColor));
        addButton(new ColorPanelButton(Skinmatics.ofSimple("foreground_color"), SkinmaticsConfig.INSTANCE.foregroundColor));
        addButton(new ColorPanelButton(Skinmatics.ofSimple("accent_color"), SkinmaticsConfig.INSTANCE.accentColor));
    }
}
