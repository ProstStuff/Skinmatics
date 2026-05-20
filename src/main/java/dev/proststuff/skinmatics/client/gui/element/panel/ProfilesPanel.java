package dev.proststuff.skinmatics.client.gui.element.panel;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.skinmatics.Profile;
import dev.proststuff.skinmatics.api.skinmatics.ProfileHandler;
import dev.proststuff.skinmatics.client.gui.element.panel.button.ProfilePanelButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public class ProfilesPanel extends Panel {
    public ProfilesPanel() {
        super(Skinmatics.of("profiles"));
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        for (Profile profile : ProfileHandler.getProfiles().values()) {
            addButton(new ProfilePanelButton(Skinmatics.of("profiles/" + profile.getName()), profile));
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
    }
}
