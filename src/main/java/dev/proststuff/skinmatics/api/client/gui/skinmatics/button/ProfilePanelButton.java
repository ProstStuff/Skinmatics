package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.utility.SkinmaticsGraphicsUtils;
import dev.proststuff.skinmatics.client.gui.customization.avatar.PreviewRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class ProfilePanelButton extends TextPanelButton {
    protected final Profile profile;

    public ProfilePanelButton(Profile profile) {
        super(SkinmaticsMod.ofSimple(profile.getProfileName()));
        this.profile = profile;
        textbox.setContent(profile.getProfileName());
    }

    public boolean isCurrentProfile() {
        return config().profile.get().equals(profile.getProfileName());
    }

    @Override
    public String getCategory() {
        return "profile";
    }

    @Override
    public MutableComponent getComponent() {
        return Component.literal(profile.getProfileName());
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            PreviewRenderer.setProfile(profile);

            if (isFocused()) {
                config().profile.set(profile.getProfileName());
                ProfileManager.loadCurrent();
            }

            return true;
        }

        return isCurrentProfile();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (isCurrentProfile()) {
            textbox.setUnit(Component.translatable("skinmatics.unit.current"));
        } else {
            textbox.setUnit(Component.empty());
        }

        renderClickableState(graphics);
        boolean rendered = SkinmaticsGraphicsUtils.head(graphics, getX() + INDENT, getY() + getHeight() / 2 - 4, profile);
        int renderSpace = (rendered ? 16 : INDENT);

        textbox.setWidth(getWidth() - renderSpace - INDENT);
        textbox.setPosition(getX() + renderSpace, getY() + (INDENT * 2) - halfLineHeight());
        textbox.render(graphics, foregroundColor(), accentColor());
    }
}