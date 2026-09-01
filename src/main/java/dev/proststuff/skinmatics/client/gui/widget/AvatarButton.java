package dev.proststuff.skinmatics.client.gui.widget;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.AvatarPlayer;
import dev.proststuff.skinmatics.client.gui.SkinmaticsGuiAssets;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsGraphicsUtils;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarData;
import dev.proststuff.skinmatics.client.skinmatics.avatar.eyes.EyeStates;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarDataManager;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarTrackerManager;
import dev.proststuff.utilitary.config.v1.ConfigManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class AvatarButton extends ClickableButton {
    public static final float HEAD_SCALE = 4.0F;
    public static final int HEAD_OFFSET = Math.round(SkinmaticsGraphicsUtils.FACE_SIZE * HEAD_SCALE) / 2;
    public static final Identifier AVATAR_GROUP = SkinmaticsMod.of("avatar");

    protected final @NonNull AvatarData avatarData;
    protected final @NonNull String avatarKey;
    protected int tickCount = 0;
    protected int blinkTick;

    public final IconButton customize;
    public final IconButton delete;
    public final IconButton open;

    public AvatarButton(int x, int y, int width, int height, AvatarData avatarData, @NonNull String avatarKey) {
        super(x, y, width, height, Component.literal(avatarData.name), AvatarButton::onClick);
        this.avatarData = avatarData;
        this.blinkTick = -avatarData.eyes().blinkingTicks();
        this.avatarKey = avatarKey;
        this.customize = new IconButton(x + width - 16, y + height - 16, SkinmaticsGuiAssets.CUSTOMIZE, 16, 16, (_) -> {});
        this.delete = new IconButton(x + width - 16, y + height - 32, SkinmaticsGuiAssets.DELETE, 16, 16, (_) -> {
            boolean success = ConfigManager.delete(avatarData.toPath(), AvatarData.TYPE);

            if (success) {
                AvatarDataManager.INSTANCE.unregister(avatarKey);
                minecraft().setScreen(parent);
            }
        });
        this.open = new IconButton(x + + width - 16, y + height - 48, SkinmaticsGuiAssets.FOLDER, 16, 16, (_) -> {

        });

        customize.setGroup(AVATAR_GROUP);
        customize.showBackground = false;
        delete.setGroup(AVATAR_GROUP);
        delete.showBackground = false;
        delete.setHolding(60);
        delete.setDirection(3);

        open.setGroup(AVATAR_GROUP);
        open.showBackground = false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        if (AvatarPlayer.hasInstance()) {
            AvatarTrackerManager.INSTANCE.register(AvatarPlayer.getInstance(), avatarData);
        }
    }

    public boolean isBlinking() {
        if (!isHoveredOrFocused()) return false;
        return blinkTick + avatarData.eyes().blinkingTicks() > tickCount;
    }

    @Override
    public boolean shouldTick() {
        return isHoveredOrFocused();
    }

    @Override
    public void tick() {
        tickCount ++;

        if (avatarData.eyes().blinkingChance() <= 0 || isBlinking()) return;
        if (SkinmaticsClient.RANDOM.nextInt(avatarData.eyes().blinkingChance()) == 0) blinkTick = tickCount;
    }

    public @NonNull AvatarData getProfile() {
        return avatarData;
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
        SkinmaticsGraphicsUtils.head(graphics, getX() + getWidth() / 2 - HEAD_OFFSET, getY() + getHeight() / 2 - HEAD_OFFSET, HEAD_SCALE, avatarData, tickCount, isBlinking() ? EyeStates.CLOSED : EyeStates.IDLE);

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
