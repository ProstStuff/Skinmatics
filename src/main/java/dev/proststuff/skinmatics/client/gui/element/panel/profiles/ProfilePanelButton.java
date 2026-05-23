package dev.proststuff.skinmatics.client.gui.element.panel.profiles;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.utility.SkinmaticsGraphicsUtil;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.skinmatics.client.gui.element.panel.button.PanelButton;
import dev.proststuff.skinmatics.client.gui.element.sidebar.button.SidebarButton;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

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
        if (Objects.equals(profile.getProfileName(), SkinmaticsConfig.INSTANCE.profile.get())) {
            graphics.fill(x, y, x + getWidth(), y + getHeight(), SkinmaticsConfig.accentColor());
            graphics.verticalLine(SidebarButton.SIZE + getWidth() - 1, y - 1, y + getHeight(), SkinmaticsConfig.foregroundColor());
        } else {
            super.render(graphics, mouseX, mouseY, tickDelta);
        }

        boolean rendered = SkinmaticsGraphicsUtil.profileHead(graphics, x + 4, y + getHeight() / 2 - 4, profile);
        int renderX = x + (rendered ? 16 : 4);
        MutableComponent component = getComponent();

        if (SkinmaticsConfig.INSTANCE.profile.get().equals(profile.getProfileName())) {
            component = Component.literal("[").append(component).append(Component.literal("]"));
        }

        GraphicsUtils.leftText(graphics, component, 0.0F, renderX, y + 8 - GraphicsUtils.font().lineHeight / 2, SkinmaticsConfig.foregroundColor());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inside(event.x(), event.y())) {
            if (selectedProfile == profile) {
                SkinmaticsConfig.INSTANCE.profile.set(profile.getProfileName());
                SkinmaticsConfig.INSTANCE.save();

                ProfileManager.loadCurrent();
            }

            AvatarPreviewRenderer.changeProfile(profile);
            selectedProfile = profile;
            return true;
        }

        return false;
    }
}
