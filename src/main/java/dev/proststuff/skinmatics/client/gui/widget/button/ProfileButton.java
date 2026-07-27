package dev.proststuff.skinmatics.client.gui.widget.button;

import dev.proststuff.skinmatics.client.gui.screen.CustomizationScreen;
import dev.proststuff.skinmatics.client.gui.screen.DeletionScreen;
import dev.proststuff.skinmatics.client.renderer.SkinmaticsRenderUtils;
import dev.proststuff.skinmatics.client.skinmatics.TrackedProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.eyes.EyeStates;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import dev.proststuff.utilitary.config.v1.ConfigResult;
import dev.proststuff.utilitary.config.v1.ConfigStatus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ProfileButton extends SimpleBackgroundButton<ProfileButton> {
    protected final String name;
    protected @Nullable Profile profile = null;
    protected ConfigStatus status = ConfigStatus.SUCCESS;
    public final IconButton customizeButton;
    public final IconButton deleteButton;

    public ProfileButton(int x, int y, int width, int height, String name) {
        super(x, y, width, height, Component.literal(name), (button, _) -> {
            if (button.isFocused()) {
                SkinmaticsConfig config = button.config();
                config.setProfile(button.name);

                TrackedProfileManager.trackClient(button.profile);
            }
        });
        this.name = name;
        customizeButton = IconButton.powerOf2(x + width - 16, y + height - 16, SkinmaticsAssets.CUSTOMIZE, 4, (_, _) -> minecraft().setScreen(new CustomizationScreen(minecraft().screen, profile)));
        deleteButton = IconButton.powerOf2(x + width - 16, y + height - 32, SkinmaticsAssets.DELETE, 4, (_, _) -> minecraft().setScreen(new DeletionScreen(minecraft().screen, name, profile)));

        customizeButton.setRenderBackground(false);
        deleteButton.setRenderBackground(false);
    }

    public boolean isClientProfile() {
        return name.equals(config().getProfile());
    }

    public void set(ConfigResult<Profile, ?> result) {
        this.profile = result.config();
        this.status = result.status();
    }

    public void setProfile(@Nullable Profile profile) {
        this.profile = profile;
    }

    @Override
    public @NonNull Component getMessage() {
        return profile != null ? Component.literal(profile.name()) : super.getMessage();
    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (customizeButton.isMouseOver(event.x(), event.y())) {
            customizeButton.onClick(event, doubleClick);
            return;
        }

        if (deleteButton.isMouseOver(event.x(), event.y())) {
            deleteButton.onClick(event, doubleClick);
            return;
        }

        super.onClick(event, doubleClick);
    }

    @Override
    public void renderBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.renderBackground(graphics, mouseX, mouseY, a);

        if (isClientProfile()) {
            graphics.outline(getX(), getY(), getWidth(), getHeight(), foregroundColor());
        }
    }

    @Override
    public void renderContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.textWithWordWrap(font(), getMessage(), getX() + SPACING, getY() + SPACING, getWidth() - SPACING * 2, getContentColor());
        float scale = 4.0F;
        int size = Math.round(SkinmaticsRenderUtils.FACE_SIZE * scale);

        if (profile != null) {
            if (status != ConfigStatus.ERROR) {
                if (minecraft().level == null) {
                    SkinmaticsRenderUtils.head(graphics, getX() + getWidth() / 2 - size / 2, getY() + getHeight() / 2 - size / 2, scale, profile, 0, EyeStates.IDLE);
                }
            } else {
                graphics.centeredText(font(), Component.translatable("skinmatics.profiles.error"), getX() + getWidth() / 2, getY() + getHeight() / 2, accentColor());
            }
        } else {
            graphics.centeredText(font(), Component.translatable("skinmatics.profiles.loading"), getX() + getWidth() / 2, getY() + getHeight() / 2, foregroundColor());
        }

        customizeButton.extractRenderState(graphics, mouseX, mouseY, a);
        deleteButton.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
