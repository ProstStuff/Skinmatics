package com.fraynn.skinmatics.gui.screen;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureType;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypeRegistry;
import com.fraynn.skinmatics.avatar.AvatarProfileManager;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import com.fraynn.skinmatics.entity.AvatarPlayer;
import com.fraynn.skinmatics.gui.screen.widget.IconButton;
import com.fraynn.skinmatics.gui.screen.widget.TextButton;
import com.fraynn.skinmatics.gui.SkinmaticsGraphicsUtils;
import com.fraynn.utilitary.client.gui.Scrollable;
import com.fraynn.utilitary.config.serialization.ConfigSerializationStatus;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

// TODO: Better rewrite this before getting 'Not even God know what it do.'

public class CustomizationScreen extends SkinmaticsScreen {
    public static final int BUTTON_WIDTH = 96;
    public static final int BUTTON_HEIGHT = 15;

    protected UiRect preview = UiRect.ZERO;
    protected UiRect featureRect = UiRect.ZERO;
    protected int previewScale = 128;

    protected final String avatarId;
    protected final AvatarProfile avatarProfile;
    protected boolean isLocked;
    protected final List<AvatarFeatureButton> featureButtons = new ArrayList<>();

    protected final long t0 = System.currentTimeMillis();

    protected TextButton save = null;

    protected final @Nullable AvatarPlayer avatar;

    public CustomizationScreen(String avatarId, AvatarProfile profile, Screen parent) {
        super(Component.translatable("skinmatics.customize", profile.name()), parent);
        this.avatarId = avatarId;
        this.isLocked = AvatarProfileManager.INSTANCE.getFallbacks().containsValue(profile);
        this.avatarProfile = profile.toMutable();
        this.avatar = AvatarPlayer.hasInstance() ? AvatarPlayer.getInstance() : null;

        if (avatar != null) {
            AvatarTrackerManager.INSTANCE.register(avatar, avatarProfile);
        }
    }

    @Override
    public void init() {
        super.init();
        int w = width / 2;
        int x = width / 8;
        preview = contentRect.withX(w - x, w + x);
        featureRect = contentRect.withX1(w / 2);

        w = featureRect.getWidth();
        int i = 0;

        for (Map.Entry<Identifier, AvatarFeatureType<?>> entry : AvatarFeatureTypeRegistry.getAll().entrySet()) {
            AvatarFeatureButton button = new AvatarFeatureButton(
                    featureRect.x0(),
                    featureRect.y0() + 16 * i,
                    w, 16,
                    entry.getValue(), avatarProfile
            );

            button.setScrollDirection(Scrollable.ScrollDirection.VERTICAL);

            featureButtons.add(button);
            button.focusable = false;
            button.showBackground = false;
            addRenderableWidget(button.hide);
            addRenderableWidget(button.add);
            addRenderableWidget(button);

            i ++;
        }

        save = new TextButton(
                width - BUTTON_WIDTH,
                height - BUTTON_HEIGHT,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                Component.translatable("skinmatics.customize.save"),
                (_) -> {
                    isLocked = false;
                    ConfigSerializationStatus status = AvatarProfileManager.INSTANCE.register(avatarId, avatarProfile.toImmutable()).save();
                    status.getException().ifPresent(exception -> Skinmatics.LOGGER.info("", exception));

                    onClose();
                });

        save.focusable = false;

        addRenderableWidget(save);
    }

    public void extractFeatures(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPanelBackground(graphics, featureRect.x0(), featureRect.y0(), featureRect.x1(), featureRect.y1());
    }

    public void extractPreview(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (avatar == null) return;
        InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, 0, 0, width, height, previewScale, 0.5F, mouseX, mouseY, avatar);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPreview(graphics, mouseX, mouseY, a);
        extractFeatures(graphics, mouseX, mouseY, a);

        extractTopBar(graphics, mouseX, mouseY, a);
        extractBottomBar(graphics, mouseX, mouseY, a);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (preview.contains(x, y)) {
            float multiplier = scrollY > 0 ? 1.1F : 0.909F;
            previewScale = Math.max(1, Math.round(previewScale * multiplier));
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    protected static final class AvatarFeatureButton extends TextButton {
        protected final AvatarFeatureType<?> avatarFeature;
        protected final AvatarProfile avatarProfile;
        protected Optional<Object> previousSettings = Optional.empty();

        protected final int cutoffWidth;
        protected final IconButton hide;
        protected final IconButton add;

        public AvatarFeatureButton(int x, int y, int width, int height, AvatarFeatureType<?> avatarFeatureType, AvatarProfile avatarProfile) {
            super(x, y, width, height, avatarFeatureType.asComponent(), (_) -> {});
            this.avatarFeature = avatarFeatureType;
            this.avatarProfile = avatarProfile;
            this.cutoffWidth = width - 32 - 8;

            this.hide = new IconButton(x + width - 32, y, 16, 16, avatarProfile.hiddenFeatures().contains(avatarFeature.id()) ? SkinmaticsGuiAssets.FEATURE_HIDDEN : SkinmaticsGuiAssets.FEATURE_SHOWN, 16, 16, (button) -> {
                boolean shouldHide = !avatarProfile.hiddenFeatures().contains(avatarFeature.id());

                if (shouldHide) {
                    avatarProfile.hiddenFeatures().add(avatarFeature.id());
                    ((IconButton) button).setTexture(SkinmaticsGuiAssets.FEATURE_HIDDEN);
                } else {
                    avatarProfile.hiddenFeatures().remove(avatarFeature.id());
                    ((IconButton) button).setTexture(SkinmaticsGuiAssets.FEATURE_SHOWN);
                }
            });

            hide.showBackground = false;
            hide.focusable = false;

            this.add = new IconButton(x + width - 16, y, 16, 16, avatarProfile.features().containsKey(avatarFeature.id()) ? SkinmaticsGuiAssets.DELETE : SkinmaticsGuiAssets.NEW_AVATAR, 16, 16, (button) -> {
                boolean shouldAdd = !avatarProfile.features().containsKey(avatarFeatureType.id());

                if (shouldAdd) {
                    avatarProfile.features().put(avatarFeatureType.id(), new AvatarFeatureData<>(avatarFeature, previousSettings));
                    hide.visible = true;
                    ((IconButton) button).setTexture(SkinmaticsGuiAssets.DELETE);
                } else {
                    previousSettings = Optional.of(avatarProfile.features().remove(avatarFeatureType.id()));
                    hide.visible = false;
                    avatarProfile.hiddenFeatures().remove(avatarFeature.id());
                    ((IconButton) button).setTexture(SkinmaticsGuiAssets.NEW_AVATAR);
                }
            });

            if (add.getTexture().equals(SkinmaticsGuiAssets.NEW_AVATAR)) hide.visible = false;
            add.showBackground = false;
            add.focusable = false;
        }

        @Override
        public @Nullable CustomizationScreen getParent() {
            return (CustomizationScreen) super.getParent();
        }

        @Override
        protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            Font font = minecraft().font;
            SkinmaticsGraphicsUtils.extractScrollingText(graphics, font, message, getX() + 4, getY() + getHeight() / 2 - font.lineHeight / 2, cutoffWidth, ARGB.color(active ? 1.0F : 0.5F, foregroundColor()), System.nanoTime() - getParent().t0);
        }
    }
}
