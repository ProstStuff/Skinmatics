package dev.proststuff.skinmatics.client.gui.element.panel;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.client.gui.element.panel.button.ProfilePanelButton;

public class ProfilesPanel extends Panel {
    public ProfilesPanel() {
        super(Skinmatics.ofSimple("profiles"));
    }

    @Override
    public void init(int width, int height) {
        buttons.clear();
        for (Profile profile : ProfileManager.getAll().values()) {
            addButton(new ProfilePanelButton(Skinmatics.ofSimple("profiles/" + profile.getName()), profile));
        }

        super.init(width, height);
    }
}