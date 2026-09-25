package dev.proststuff.skinmatics.gui;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.entity.AvatarPlayer;
import dev.proststuff.skinmatics.avatar.v1.data.AvatarData;
import dev.proststuff.skinmatics.gui.widget.*;
import dev.proststuff.skinmatics.manager.AvatarDataManager;
import dev.proststuff.skinmatics.manager.AvatarTrackerManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvatarScreen extends SkinmaticsScreen {
    public static final Component TITLE = Component.translatable("skinmatics.avatar");

    protected List<ClickableButton> avatarListButtons = new ArrayList<>();
    protected boolean avatarAvailable = false;
    protected UiRect avatarList = UiRect.ZERO;
    protected double maxAvatarListYOffset = 0;
    protected double avatarListYOffset = 0;

    protected UiRect preview = UiRect.ZERO;
    protected int previewScale = 128;

    public AvatarScreen(Screen parent) {
        super(TITLE, parent);
    }

    public void updateScroll(double increment) {
        avatarListYOffset = Math.clamp(avatarListYOffset + increment, -maxAvatarListYOffset, 0);

        avatarListButtons.forEach(button -> {
            int offset = (int) avatarListYOffset;
            button.setY(button.getOriginY() + offset);

            if (button instanceof AvatarButton avatar) {
                avatar.customize.setY(avatar.customize.getOriginY() + offset);
                avatar.delete.setY(avatar.delete.getOriginY() + offset);
            }
        });
    }

    @Override
    public void init() {
        avatarListButtons.clear();
        AvatarPlayer.discardInstance();
        super.init();

        this.avatarAvailable = minecraft.level != null;
        if (avatarAvailable) AvatarPlayer.createInstance();
        int contentWidth = contentRect.getWidth();
        this.preview = contentRect.withX0(contentWidth + (avatarAvailable ? - contentWidth / 4 : SPACING));
        this.avatarList = contentRect.withX1(preview.x0() - SPACING);

        int avatarXIndex = config().getGui().getHorizontalProfiles();
        int avatarYIndex = config().getGui().getVerticalProfiles();
        int avatarWidth = Math.round((avatarList.getWidth() + 1.0F) / avatarXIndex);
        int avatarHeight = avatarList.getHeight() / avatarYIndex;
        int i = 0;
        int j = 0;

        for (Map.Entry<String, String> entry : AvatarDataManager.INSTANCE.getFailures().entrySet()) {
            FailedAvatarButton button = new FailedAvatarButton(
                    contentRect.x0() + (i * avatarWidth),
                    contentRect.y0() + (j * avatarHeight),
                    avatarWidth, avatarHeight,
                    entry.getKey(),
                    entry.getValue()
            );

            addWidget(button);
            avatarListButtons.add(button);

            i ++;

            if (i >= avatarXIndex) {
                i = 0;
                j ++;
            }
        }

        AvatarButton selectedAvatar = null;

        for (Map.Entry<String, AvatarData> entry : AvatarDataManager.INSTANCE.getAll().entrySet()) {
            AvatarButton button = new AvatarButton(
                    contentRect.x0() + (i * avatarWidth),
                    contentRect.y0() + (j * avatarHeight),
                    avatarWidth, avatarHeight,
                    entry.getValue(),
                    entry.getKey()
            );

            addWidget(button.customize);
            addWidget(button.delete);
            addWidget(button);

            avatarListButtons.add(button);
            if (button.isSelectedProfile()) {
                selectedAvatar = button;
            }

            i ++;

            if (i >= avatarXIndex) {
                i = 0;
                j ++;
            }
        }

        if (selectedAvatar != null) {
            setFocused(selectedAvatar);
            if (avatarAvailable) AvatarTrackerManager.INSTANCE.register(AvatarPlayer.getInstance(), selectedAvatar.getProfile());
        }

        NewAvatarButton button = new NewAvatarButton(
                contentRect.x0() + (i * avatarWidth),
                contentRect.y0() + (j * avatarHeight),
                avatarWidth, avatarHeight
        );
        addWidget(button);
        avatarListButtons.add(button);
        i ++;
        if (i >= avatarXIndex) {
            j ++;
        }

        this.maxAvatarListYOffset = Math.max(j - avatarYIndex + 1, 0) * avatarHeight;

        addRenderableWidget(
                new TextButton(width - 32, 0, 16, 16, Component.literal("?"), (_) -> Util.getPlatform().openUri("https://fraynn.pages.dev/docs/category/skinmatics"))
        ).focusable = false;
    }

    public void extractAvatars(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPanelBackground(graphics, avatarList.x0(), avatarList.y0(), avatarList.x1(), avatarList.y1());
        graphics.enableScissor(avatarList.x0(), avatarList.y0() + 1, avatarList.x1(), avatarList.y1() - 1);

        for (ClickableButton button : avatarListButtons) {
            button.extractRenderState(graphics, mouseX, mouseY, a);
        }

        graphics.disableScissor();
    }

    public void extractPreview(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!avatarAvailable) return;
        extractPanelBackground(graphics, preview.x0(), preview.y0(), preview.x1(), preview.y1());
        graphics.enableScissor(preview.x0(), preview.y0() + 1, preview.x1(), preview.y1() - 1);

        if (minecraft.player != null) {
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, preview.x0(), preview.y0(), preview.x1(), preview.y1(), previewScale, 0.5F, mouseX, mouseY, AvatarPlayer.getInstance());
        }

        graphics.disableScissor();
    }

    @Override
    public void extractBottomBar(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBottomBar(graphics, mouseX, mouseY, a);
        int y = height - BAR_HEIGHT + font.lineHeight / 2;
        if (AvatarDataManager.INSTANCE.get(config().getAvatar()) == null) {
            graphics.text(font, Component.translatable("skinmatics.avatar.no_avatar"), BAR_X_INSET, y, foregroundColor());
        } else {
            graphics.text(font, Component.literal("Skinmatics " + Skinmatics.getVersionString()), BAR_X_INSET, y, foregroundColor(BACKGROUND_ALPHA));
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTopBar(graphics, mouseX, mouseY, a);
        extractAvatars(graphics, mouseX, mouseY, a);
        extractPreview(graphics, mouseX, mouseY, a);
        extractBottomBar(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (avatarList.contains(event.x(), event.y())) {
            updateScroll(dy);
            return true;
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (avatarList.contains(x, y)) {
            updateScroll(scrollY * 16.0);
            return true;
        } else if (preview.contains(x, y)) {
            float multiplier = scrollY > 0 ? 1.1F : 0.909F;
            previewScale = Math.max(1, Math.round(previewScale * multiplier));
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}
