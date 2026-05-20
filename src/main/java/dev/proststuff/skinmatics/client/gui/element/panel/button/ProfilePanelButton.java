package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.api.skinmatics.Profile;
import dev.proststuff.skinmatics.api.skinmatics.SkinmaticsConfig;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ProfilePanelButton extends PanelButton {
    protected final Profile profile;

    public ProfilePanelButton(Identifier identifier, Profile profile) {
        super(identifier, () -> Component.literal(profile.getName()));
        this.profile = profile;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        GraphicsUtils.leftText(graphics, profile.getName(), 0.5F, x + 4, y + getHeight() / 2, SkinmaticsConfig.foregroundColor());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            if (doubleClick) {
                SkinmaticsConfig.INSTANCE.profile.set(profile.getName());
            } else {

            }
        }

        return false;
    }
}
