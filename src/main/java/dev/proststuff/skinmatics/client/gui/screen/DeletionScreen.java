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
import org.jspecify.annotations.Nullable;

public class DeletionScreen extends AbstractSkinmaticsScreen {
    protected final String fileName;
    protected final @Nullable Profile profile;

    protected EditBox fileLocationBox;
    protected TextButton deleteButton;

    public DeletionScreen(Screen parent, String fileName, @Nullable Profile profile) {
        super(Component.translatable("skinmatics.delete", profile != null ? profile.name() : fileName), parent);
        this.fileName = fileName;
        this.profile = profile;
    }

    @Override
    public void init() {
        int boxWidth = width - INSET * 8;
        int x = width / 2 - boxWidth / 2;

        TextWidget text = addRenderableOnly(new TextWidget(x, INSET * 4, boxWidth, 16, Component.translatable("skinmatics.delete.text", profile != null ? profile.name() : fileName, fileName), TextAlignment.LEFT, TextAlignment.CENTER)).setScrollDirection(ScrollDirection.LOCKED);
        fileLocationBox = new EditBox(font(), boxWidth, 16, Component.empty());
        fileLocationBox.setHint(Component.literal(ProfileUtils.toProfilePath("[...]", true)));
        fileLocationBox.setPosition(x, text.getY() + text.getHeight() + SPACING);
        addRenderableWidget(fileLocationBox);

        int w = boxWidth / 2;
        this.deleteButton = addRenderableWidget(
                new TextButton(x, INSET * 10 - 8, w, 16, Component.translatable("skinmatics.delete.confirm"), (_, _) -> {
                    ProfileManager.delete(fileName);
                    onClose();
                })
        );
        deleteButton.active = false;

        addRenderableWidget(
                new TextButton(x + w, INSET * 10 - 8, w, 16, Component.translatable("skinmatics.new_profile.cancel"), (_, _) -> onClose())
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(INSET, 0, width - INSET, height, backgroundColor());

        int centerX = width / 2;
        graphics.centeredText(font, getTitle(), centerX, INSET - font.lineHeight / 2, foregroundColor());
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        boolean result = super.charTyped(event);

        if (result) {
            deleteButton.active = fileLocationBox.getValue().equals(String.format("profiles/%s.json", fileName));
        }

        return result;
    }
}
