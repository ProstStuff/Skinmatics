package dev.proststuff.skinmatics.gui;

import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.gui.widget.TextButton;
import dev.proststuff.skinmatics.avatar.manager.AvatarProfileManager;
import dev.proststuff.skinmatics.util.SkinmaticsGraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class NewAvatarScreen extends SkinmaticsScreen {
    public static final Component TITLE = Component.translatable("skinmatics.new_avatar");
    public static final int BUTTON_WIDTH = 64;
    public static final int BUTTON_HEIGHT = 15;

    protected Component pathStatus = Component.empty();
    protected UiRect bottomTextRect = UiRect.ZERO;
    protected long t0 = System.currentTimeMillis();

    protected EditBox avatarPath;
    protected EditBox displayName;
    protected TextButton create;

    public NewAvatarScreen(Screen parent) {
        super(TITLE, parent);
    }

    public void updateFields() {
        displayName.setHint(Component.literal(List.of(avatarPath.getValue().split("/")).getLast()));

        String path = avatarPath.getValue();
        if (path.isBlank()) {
            create.active = false;
            pathStatus = Component.translatable("skinmatics.new_avatar.path_empty");
            return;
        }
        create.active = true;
        boolean found = AvatarProfileManager.INSTANCE.keys().contains(path);
        path = AvatarUtils.toAvatarPath(path, true);

        if (found) {
            pathStatus = Component.translatable("skinmatics.new_avatar.path_existed", path);
            create.setHolding(20);
        } else {
            pathStatus = Component.translatable("skinmatics.new_avatar.path_available", path);
            create.setHolding(0);
        }
    }

    @Override
    public void init() {
        super.init();
        bottomTextRect = bottomBarRect.withX(BAR_X_INSET, bottomBarRect.x1() - BUTTON_WIDTH - SPACING);

        int s = SPACING / 2;
        contentRect = contentRect.withY(contentRect.getCenterY() - 48, contentRect.getCenterY() + 48);
        int w = contentRect.getWidth() - SPACING * 2;

        this.avatarPath = addRenderableWidget(
                new EditBox(font, contentRect.x0() + SPACING, contentRect.getCenterY() - 16 - s, w, 16, Component.empty())
        );

        this.displayName = addRenderableWidget(
                new EditBox(font, contentRect.x0() + SPACING, contentRect.getCenterY() + s + 16, w, 16, Component.empty())
        );

        this.create = addRenderableWidget(
                new TextButton(
                        width - BUTTON_WIDTH,
                        height - BUTTON_HEIGHT,
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        Component.translatable("skinmatics.new_avatar.create"),
                        (_) -> {
                            String path = avatarPath.getValue();
                            if (path.isBlank()) return;
                            String last = List.of(path.split("/")).getLast();
                            String name = displayName.getValue();
                            name = name.isBlank() ? last : name;
                            AvatarProfileManager.INSTANCE.register(path, new AvatarProfile(name)).save();
                            onClose();
                        }
                )
        );

        this.avatarPath.setValue(minecraft.getGameProfile().name());
        updateFields();
    }

    public void extractAvatarCreation(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPanelBackground(graphics, contentRect.x0(), contentRect.y0(), contentRect.x1(), contentRect.y1());
        graphics.text(font, Component.translatable("skinmatics.new_avatar.path"), contentRect.x0() + SPACING, contentRect.getCenterY() - 28 - SPACING, foregroundColor());
        graphics.text(font, Component.translatable("skinmatics.new_avatar.display_name"), contentRect.x0() + SPACING, contentRect.getCenterY() + 4, foregroundColor());
    }

    @Override
    public void extractBottomBar(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBottomBar(graphics, mouseX, mouseY, a);
        int y = height - BAR_HEIGHT + font.lineHeight / 2;
        if (bottomTextRect.contains(mouseX, mouseY)) {
            SkinmaticsGraphicsUtils.extractScrollingText(graphics, font, pathStatus, bottomTextRect.x0(), y, bottomTextRect.getWidth(), foregroundColor(), System.nanoTime() - t0);
        } else {
            t0 = System.nanoTime();
            graphics.enableScissor(bottomTextRect.x0(), bottomTextRect.y0(), bottomTextRect.x1(), bottomTextRect.y1());
            graphics.text(font, pathStatus, bottomTextRect.x0(), y, foregroundColor());
            graphics.disableScissor();
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTopBar(graphics, mouseX, mouseY, a);
        extractAvatarCreation(graphics, mouseX, mouseY, a);
        extractBottomBar(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        boolean status = super.charTyped(event);
        if (status) updateFields();
        return status;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        boolean status = super.keyPressed(event);
        if (status) updateFields();
        return status;
    }
}