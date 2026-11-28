package com.fraynn.skinmatics.gui.screen;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.fraynn.skinmatics.avatar.feature.type.eye.EyeUtils;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import com.fraynn.skinmatics.entity.AvatarPlayer;
import com.fraynn.skinmatics.gui.screen.widget.*;
import com.fraynn.skinmatics.avatar.AvatarProfileManager;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import com.fraynn.skinmatics.gui.SkinmaticsGraphicsUtils;
import com.fraynn.utilitary.config.serialization.ConfigParser;
import com.fraynn.utilitary.config.serialization.ConfigSerializationStatus;
import com.fraynn.utilitary.registry.SimpleRegistry;
import com.fraynn.utilitary.util.SimpleIdentifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
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

        for (ClickableButton button : avatarListButtons) {
            removeWidget(button);
        }

        avatarListButtons.clear();
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

        for (Map.Entry<String, String> entry : AvatarProfileManager.INSTANCE.getFailures().entrySet()) {
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

        for (SimpleRegistry.Entry<String, AvatarProfile> entry : AvatarProfileManager.INSTANCE.entries()) {
            AvatarButton button = new AvatarButton(
                    contentRect.x0() + (i * avatarWidth),
                    contentRect.y0() + (j * avatarHeight),
                    avatarWidth, avatarHeight,
                    entry.value(),
                    entry.key()
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
            if (avatarAvailable) AvatarTrackerManager.INSTANCE.register(AvatarPlayer.getInstance(), selectedAvatar.getAvatar());
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
                new TextButton(width - 32, 0, 16, 16, Component.literal("?"), (_) -> Util.getPlatform().openUri("https://fraynn.pages.dev/skinmatics/intro"))
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
        if (AvatarPlayer.hasInstance()) InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, preview.x0(), preview.y0(), preview.x1(), preview.y1(), previewScale, 0.5F, mouseX, mouseY, AvatarPlayer.getInstance());
        graphics.disableScissor();
    }

    @Override
    public void extractBottomBar(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBottomBar(graphics, mouseX, mouseY, a);
        int y = height - BAR_HEIGHT + font.lineHeight / 2;
        if (AvatarProfileManager.INSTANCE.get(config().getAvatar()) == null) {
            graphics.text(font, Component.translatable("skinmatics.avatar.no_avatar"), BAR_X_INSET, y, foregroundColor());
        } else {
            graphics.text(font, Component.literal(Skinmatics.INFO.name() + " " + Skinmatics.INFO.version()), BAR_X_INSET, y, foregroundColor(BACKGROUND_ALPHA));
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

    public static class AvatarButton extends ClickableButton {
        public static final float HEAD_SCALE = 4.0F;
        public static final int HEAD_OFFSET = Math.round(SkinmaticsGraphicsUtils.FACE_SIZE * HEAD_SCALE) / 2;
        public static final Identifier AVATAR_GROUP = Skinmatics.of("avatar");

        protected final @NonNull AvatarProfile profile;
        protected final @NonNull String avatarId;
        protected int tickCount = 0;

        public final IconButton customize;
        public final IconButton delete;

        public AvatarButton(int x, int y, int width, int height, AvatarProfile profile, @NonNull String avatarId) {
            super(x, y, width, height, Component.literal(profile.name()), AvatarButton::onClick);
            this.profile = profile;
            this.avatarId = avatarId;

            this.customize = new IconButton(x + width - 16, y + height - 16, SkinmaticsGuiAssets.CUSTOMIZE, 16, 16, (_) -> minecraft().setScreenAndShow(new CustomizationScreen(avatarId, profile, getParent())));

            this.delete = new IconButton(x + width - 16, y + height - 32, SkinmaticsGuiAssets.DELETE, 16, 16, (_) -> {
                SimpleIdentifier path = profile.toPath();
                ConfigSerializationStatus status = ConfigParser.erase(AvatarProfile.TYPE, path);

                if (status.isSuccessful()) {
                    AvatarProfileManager.INSTANCE.unregister(avatarId);

                    if (AvatarProfileManager.INSTANCE.getFallbacks().containsKey(avatarId)) {
                        AvatarProfileManager.INSTANCE.register(avatarId, AvatarProfileManager.INSTANCE.getFallbacks().get(avatarId));
                    }

                    minecraft().setScreenAndShow(parent);
                }
            });

            if (AvatarProfileManager.INSTANCE.getFallbacks().containsValue(profile)) {
                delete.active = false;
                delete.setTooltip(Tooltip.create(Component.translatable("skinmatics.avatar.no_delete")));
            }

            customize.setGroup(AVATAR_GROUP);
            customize.showBackground = false;
            delete.setGroup(AVATAR_GROUP);
            delete.showBackground = false;
            delete.setHolding(60);
            delete.setDirection(3);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);

            if (AvatarPlayer.hasInstance()) {
                AvatarTrackerManager.INSTANCE.register(AvatarPlayer.getInstance(), profile);
            }
        }

        @Override
        public boolean shouldTick() {
            return isHoveredOrFocused();
        }

        @Override
        public void tick() {
            tickCount ++;
        }

        public @NonNull AvatarProfile getAvatar() {
            return profile;
        }

        public @NonNull String getAvatarId() {
            return avatarId;
        }

        public boolean isSelectedProfile() {
            return avatarId.equals(SkinmaticsConfig.getInstance().getAvatar());
        }

        @Override
        protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            if (isHoveredOrFocused()) {
                customize.extractRenderState(graphics, mouseX, mouseY, a);
                delete.extractRenderState(graphics, mouseX, mouseY, a);
            }

            Font font = minecraft().font;
            SkinmaticsGraphicsUtils.head(graphics, getX() + getWidth() / 2 - HEAD_OFFSET, getY() + getHeight() / 2 - HEAD_OFFSET, HEAD_SCALE, profile, tickCount, EyeUtils.STATE_IDLE);

            graphics.centeredText(font, getMessage(), getX() + getWidth() / 2, getY() + 8, foregroundColor());

            if (isSelectedProfile()) {
                graphics.outline(getX(), getY(), getWidth(), getHeight(), foregroundColor());
            }
        }

        public static void onClick(ClickableButton widget) {
            if (!(widget instanceof AvatarButton button)) return;
            if (widget.isFocused()) SkinmaticsConfig.getInstance().setAvatar(button.getAvatarId());
        }
    }

    public static class NewAvatarButton extends ClickableButton {
        public NewAvatarButton(int x, int y, int width, int height) {
            super(x, y, width, height, Component.translatable("skinmatics.avatar.new"), NewAvatarButton::onClick);
        }

        @Override
        protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            int centerX = getX() + getWidth() / 2;
            Font font = minecraft().font;
            graphics.centeredText(font, getMessage(), centerX, getY() + 8, foregroundColor());
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    SkinmaticsGuiAssets.NEW_AVATAR,
                    centerX - 16,
                    getY() + getHeight() / 2 - 16,
                    0.0F,
                    0.0F,
                    32,
                    32,
                    32,
                    32,
                    32,
                    32,
                    0xFFFFFFFF
            );
        }

        public static void onClick(ClickableButton widget) {
            Minecraft.getInstance().setScreenAndShow(new NewAvatarScreen(widget.getParent()));
        }
    }

    public static class FailedAvatarButton extends ClickableButton {
        protected String error;

        public FailedAvatarButton(int x, int y, int width, int height, String name, String error) {
            super(x, y, width, height, Component.literal(name).withStyle(ChatFormatting.RED), FailedAvatarButton::onClick);
            this.focusable = false;
            this.error = error;

            setTooltip(Tooltip.create(Component.translatable("skinmatics.avatar.error", error)));
            setTooltipDelay(Duration.ZERO);
        }

        @Override
        protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            int centerX = getX() + getWidth() / 2;
            Font font = minecraft().font;
            graphics.centeredText(font, getMessage(), centerX, getY() + 8, foregroundColor());
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    SkinmaticsGuiAssets.ERROR,
                    centerX - 16,
                    getY() + getHeight() / 2 - 16,
                    0.0F,
                    0.0F,
                    32,
                    32,
                    32,
                    32,
                    32,
                    32,
                    0xFFFFFFFF
            );
        }

        public static void onClick(ClickableButton widget) {}
    }
}
