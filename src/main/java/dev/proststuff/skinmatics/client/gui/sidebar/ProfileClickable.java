package dev.proststuff.skinmatics.client.gui.sidebar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.panel.ProfilePanel;
import net.minecraft.network.chat.Component;

public class ProfileClickable extends SidebarClickable {
    public ProfileClickable() {
        super(Skinmatics.of("profiles"), Skinmatics.of("textures/gui/skinmatics.png"), ProfilePanel::new);
    }

    @Override
    public Component asComponent() {
        return Component.translatable(getIdentifier().toLanguageKey(), SkinmaticsConfig.INSTANCE.profile.get());
    }
}
