package dev.proststuff.skinmatics.client.gui.panel;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.panel.clickable.DescriptionPanelClickable;
import dev.proststuff.skinmatics.client.gui.sidebar.SidebarClickable;

public class SkinmaticsPanel extends Panel {
    public SkinmaticsPanel(SidebarClickable sidebarClickable) {
        super(sidebarClickable);
        addClickable(new DescriptionPanelClickable(Skinmatics.of("profile")));
        addClickable(new DescriptionPanelClickable(Skinmatics.of("config"), SkinmaticsConfig.INSTANCE::getDestination, true));

    }

    @Override
    public void setDimensions(int width, int height) {
        super.setDimensions(width, height);
    }
}
