package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.api.Profile;
import dev.proststuff.skinmatics.api.ProfileHandler;
import dev.proststuff.skinmatics.api.SkinmaticsConfig;
import dev.proststuff.skinmatics.api.utility.SkinmaticsGraphicsUtil;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class ProfilePanelButton extends PanelButton {
    protected static Profile selectedProfile = null;

    protected final Profile profile;

    public ProfilePanelButton(SimpleIdentifier identifier, Profile profile) {
        super(identifier, () -> Component.literal(profile.getProfileName()));
        this.profile = profile;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
        boolean rendered = SkinmaticsGraphicsUtil.profileHead(graphics, x + 4, y + getHeight() / 2 - 4, profile);
        int renderX = x + (rendered ? 16 : 4);
        MutableComponent component = getComponent();

        if (SkinmaticsConfig.INSTANCE.profile.get().equals(profile.getProfileName())) {
            component = Component.literal("[").append(component).append(Component.literal("]"));
        }

        GraphicsUtils.text(graphics, component, 0.0F, 0.5F, renderX, y + getHeight() / 2, SkinmaticsConfig.foregroundColor());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inside(event.x(), event.y())) {
            if (selectedProfile == profile) {
                SkinmaticsConfig.INSTANCE.profile.set(profile.getProfileName());
                SkinmaticsConfig.INSTANCE.save();

                if (Minecraft.getInstance().player != null) {
                    ProfileHandler.loadCurrentProfile(Minecraft.getInstance().player);
                }
            }

            AvatarPreviewRenderer.changeProfile(profile);
            selectedProfile = profile;
            return true;
        }

        return false;
    }
}
