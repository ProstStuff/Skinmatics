package dev.proststuff.skinmatics.gui.widget;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.feature.type.eye.EyeUtils;
import dev.proststuff.skinmatics.avatar.manager.AvatarProfileManager;
import dev.proststuff.skinmatics.avatar.manager.AvatarTrackerManager;
import dev.proststuff.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.entity.AvatarPlayer;
import dev.proststuff.skinmatics.gui.SkinmaticsGuiAssets;
import dev.proststuff.skinmatics.util.SkinmaticsGraphicsUtils;
import dev.proststuff.utilitary.config.serialization.ConfigParser;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationStatus;
import dev.proststuff.utilitary.util.SimpleIdentifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class AvatarButton extends ClickableButton {
    public static final float HEAD_SCALE = 4.0F;
    public static final int HEAD_OFFSET = Math.round(SkinmaticsGraphicsUtils.FACE_SIZE * HEAD_SCALE) / 2;
    public static final Identifier AVATAR_GROUP = Skinmatics.of("avatar");

    protected final @NonNull AvatarProfile avatarProfile;
    protected final @NonNull String avatarKey;
    protected int tickCount = 0;

    public final IconButton customize;
    public final IconButton delete;

    public AvatarButton(int x, int y, int width, int height, AvatarProfile avatarProfile, @NonNull String avatarKey) {
        super(x, y, width, height, Component.literal(avatarProfile.name()), AvatarButton::onClick);
        this.avatarProfile = avatarProfile;
        this.avatarKey = avatarKey;

        this.customize = new IconButton(x + width - 16, y + height - 16, SkinmaticsGuiAssets.CUSTOMIZE, 16, 16, (_) -> {});
        this.customize.active = false;
        this.customize.setTooltip(Tooltip.create(Component.translatable("skinmatics.avatar.edit_wip").withStyle(ChatFormatting.ITALIC)));

        this.delete = new IconButton(x + width - 16, y + height - 32, SkinmaticsGuiAssets.DELETE, 16, 16, (_) -> {
            SimpleIdentifier path = avatarProfile.toPath();
            ConfigSerializationStatus status = ConfigParser.erase(AvatarProfile.TYPE, path);

            if (status.isSuccessful()) {
                AvatarProfileManager.INSTANCE.unregister(avatarKey);
                minecraft().setScreen(parent);
            }
        });

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
            AvatarTrackerManager.INSTANCE.register(AvatarPlayer.getInstance(), avatarProfile);
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
        return avatarProfile;
    }

    public @NonNull String getAvatarKey() {
        return avatarKey;
    }

    public boolean isSelectedProfile() {
        return avatarKey.equals(SkinmaticsConfig.getInstance().getAvatar());
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (isHoveredOrFocused()) {
            customize.extractRenderState(graphics, mouseX, mouseY, a);
            delete.extractRenderState(graphics, mouseX, mouseY, a);
        }

        Font font = minecraft().font;
        SkinmaticsGraphicsUtils.head(graphics, getX() + getWidth() / 2 - HEAD_OFFSET, getY() + getHeight() / 2 - HEAD_OFFSET, HEAD_SCALE, avatarProfile, tickCount, EyeUtils.STATE_IDLE);

        graphics.centeredText(font, getMessage(), getX() + getWidth() / 2, getY() + 8, foregroundColor());

        if (isSelectedProfile()) {
            graphics.outline(getX(), getY(), getWidth(), getHeight(), foregroundColor());
        }
    }

    public static void onClick(ClickableButton widget) {
        if (!(widget instanceof AvatarButton button)) return;
        if (widget.isFocused()) SkinmaticsConfig.getInstance().setAvatar(button.getAvatarKey());
    }
}
