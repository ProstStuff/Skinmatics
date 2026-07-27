package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.client.gui.widget.TextWidget;
import dev.proststuff.skinmatics.client.gui.widget.button.TextButton;
import dev.proststuff.skinmatics.client.skinmatics.ProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class CreationScreen extends AbstractSkinmaticsScreen {
    protected EditBox profileNameBox;
    protected EditBox fileLocationBox;

    protected TextButton createButton;

    public CreationScreen(Screen parent) {
        super(Component.translatable("skinmatics.new_profile"), parent);
    }

    public String getFileLocation() {
        String loc = fileLocationBox.getValue();
        return loc.isBlank() ? profileNameBox.getValue() : loc;
    }

    public ComponentStatus getStatus() {
        if (profileNameBox == null) return new ComponentStatus(Component.empty(), false);
        String profileName = profileNameBox.getValue();
        String fileLocation = getFileLocation();
        String completeFileLocation = ProfileUtils.toProfilePath(fileLocation, true);

        if (ProfileManager.LOADED_PROFILES.containsKey(fileLocation)) {
            return new ComponentStatus(Component.translatable("skinmatics.new_profile.profile_exist", completeFileLocation, ProfileManager.LOADED_PROFILES.get(fileLocation).name()), false);
        } else if (profileName.isBlank()) {
            return new ComponentStatus(Component.translatable("skinmatics.new_profile.empty"), false);
        }

        return new ComponentStatus(Component.translatable("skinmatics.new_profile.result", completeFileLocation, profileName), true);
    }

    @Override
    public void init() {
        int boxWidth = width - INSET * 8;
        int x = width / 2 - boxWidth / 2;

        profileNameBox = new EditBox(font(), boxWidth, 16, Component.empty());
        profileNameBox.setPosition(x, INSET * 5);

        fileLocationBox = new EditBox(font(), boxWidth, 16, Component.empty());
        fileLocationBox.setPosition(x, INSET * 7);

        profileNameBox.setValue(minecraft().getGameProfile().name());
        fileLocationBox.setHint(Component.literal(profileNameBox.getValue()));

        addRenderableOnly(new TextWidget(x, INSET * 4, boxWidth, 16, Component.translatable("skinmatics.new_profile.name"), TextAlignment.LEFT, TextAlignment.CENTER)).setScrollDirection(ScrollDirection.LOCKED);
        addRenderableOnly(new TextWidget(x, INSET * 6, boxWidth, 16, Component.translatable("skinmatics.new_profile.file_name"), TextAlignment.LEFT, TextAlignment.CENTER)).setScrollDirection(ScrollDirection.LOCKED);

        addRenderableWidget(profileNameBox);
        addRenderableWidget(fileLocationBox);

        int w = boxWidth / 2;
        createButton = addRenderableWidget(
                new TextButton(x, INSET * 10 - 8, w, 16, Component.translatable("skinmatics.new_profile.create"), (_, _) -> {
                    Profile profile = new Profile(profileNameBox.getValue());
                    ProfileManager.save(getFileLocation(), profile);
                    onClose();
                })
        );
        addRenderableWidget(
                new TextButton(x + w, INSET * 10 - 8, w, 16, Component.translatable("skinmatics.new_profile.cancel"), (_, _) -> onClose())
        );
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ComponentStatus status = getStatus();
        createButton.active = status.success;

        graphics.fill(INSET, 0, width - INSET, height, backgroundColor());

        int centerX = width / 2;
        graphics.centeredText(font, getTitle(), centerX, INSET - font.lineHeight / 2, foregroundColor());
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.textWithWordWrap(font, status.component, SPACING + fileLocationBox.getX(), SPACING + INSET * 8, width - INSET * 8 - SPACING, status.success ? foregroundColor() : accentColor());
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        boolean result = super.charTyped(event);
        if (result) {
            fileLocationBox.setHint(Component.literal(profileNameBox.getValue()));
        }

        return result;
    }

    public record ComponentStatus(Component component, boolean success) { }
}
