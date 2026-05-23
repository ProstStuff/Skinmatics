package dev.proststuff.skinmatics.client.gui.element.panel.profiles;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public class ProfilesPanel extends Panel {
    protected static boolean rescan = true;

    public ProfilesPanel() {
        super(Skinmatics.ofSimple("profiles"));
    }

    public void scan() {
        if (rescan) {
            buttons.clear();
            for (Profile profile : ProfileManager.getAll().values()) {
                addButton(new ProfilePanelButton(Skinmatics.ofSimple("profiles/" + profile.getName()), profile));
            }
            addButton(new NewProfilePanelButton());

            rescan = false;
        }
    }

    @Override
    public void init(int width, int height) {
        rescan();
        scan();

        super.init(width, height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (rescan) {
            scan();
        }

        super.render(graphics, mouseX, mouseY, tickDelta);
    }

    public static void rescan() {
        rescan = true;
    }
}