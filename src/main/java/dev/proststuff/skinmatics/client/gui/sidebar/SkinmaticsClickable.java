package dev.proststuff.skinmatics.client.gui.sidebar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.gui.panel.SkinmaticsPanel;
import net.minecraft.network.chat.Component;

public class SkinmaticsClickable extends SidebarClickable {
    public SkinmaticsClickable() {
        super(Skinmatics.of("skinmatics"), Skinmatics.of("textures/icon.png"), SkinmaticsPanel::new);
    }

    @Override
    public Component asComponent() {
        return Component.translatable(getIdentifier().toLanguageKey(), Skinmatics.getVersion());
    }
}
