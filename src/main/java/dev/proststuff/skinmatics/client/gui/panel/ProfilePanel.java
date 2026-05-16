package dev.proststuff.skinmatics.client.gui.panel;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.gui.panel.clickable.DescriptionPanelClickable;
import dev.proststuff.skinmatics.client.gui.sidebar.SidebarClickable;
import dev.proststuff.skinmatics.client.skinmatics.ProfileHandler;
import dev.proststuff.utilitary.utility.UtilitaryJsonUtils;
import net.minecraft.client.Minecraft;

public class ProfilePanel extends Panel{
    public ProfilePanel(SidebarClickable sidebarClickable) {
        super(sidebarClickable);
        addClickable(new DescriptionPanelClickable(Skinmatics.of("profile")));
        addClickable(new  DescriptionPanelClickable(Skinmatics.of("file"), () -> ProfileHandler.getCurrentProfile().getDestination(), true));
        addClickable(new DescriptionPanelClickable(Skinmatics.of("textures"), () -> UtilitaryJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("textures"), false));
        addClickable(new DescriptionPanelClickable(Skinmatics.of("load"), (_) -> Minecraft.getInstance().reloadResourcePacks()));
        addClickable(new DescriptionPanelClickable(Skinmatics.of("documentation"), "https://github.com/ProstStuff/Skinmatics"));
    }
}
