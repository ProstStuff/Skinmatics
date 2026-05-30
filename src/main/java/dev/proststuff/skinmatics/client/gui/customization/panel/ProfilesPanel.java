package dev.proststuff.skinmatics.client.gui.customization.panel;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.ProfilePanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;

public class ProfilesPanel extends AbstractPanel {
    public ProfilesPanel() {
        super(SkinmaticsMod.ofSimple("profiles"), false);
    }

    @Override
    public void init(int width, int height) {
        buttons.clear();

        for (Profile profile : ProfileManager.scanAndLoad().values()) {
            ProfilePanelButton button = addButton(new ProfilePanelButton(profile));

            if (profile.getProfileName().equals(config().profile.get())) {
                button.focus();
            }
        }

        super.init(width, height);
    }

    @Override
    public boolean shouldUnfocusWhenClicked() {
        return false;
    }
}
