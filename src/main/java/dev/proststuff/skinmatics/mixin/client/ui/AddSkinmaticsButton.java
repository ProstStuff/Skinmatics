package dev.proststuff.skinmatics.mixin.client.ui;

import dev.proststuff.skinmatics.avatar.v1.data.AvatarData;
import dev.proststuff.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.gui.SkinmaticsGui;
import dev.proststuff.skinmatics.gui.SkinmaticsGuiAssets;
import dev.proststuff.skinmatics.gui.widget.IconButton;
import dev.proststuff.skinmatics.manager.AvatarDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsSubScreen.class)
public class AddSkinmaticsButton {
    @Shadow
    @Final
    public HeaderAndFooterLayout layout;
    @Unique private AbstractWidget skinmatics$skinmaticsButton;

    @Inject(method = "addFooter", at = @At("TAIL"))
    public void skinmatics$addSkinmatics(CallbackInfo ci) {
        OptionsSubScreen screen = (OptionsSubScreen) (Object)this;

        if (screen instanceof SkinCustomizationScreen) {
            String avatarName = SkinmaticsConfig.getInstance().getAvatar();
            AvatarData avatarData = AvatarDataManager.INSTANCE.get(avatarName);

            if (avatarData != null) {
                avatarName = avatarData.getDisplayName();
            } else {
                avatarName = "";
            }

            IconButton button = new IconButton(0, 0, 20, 20, SkinmaticsGuiAssets.SKINMATICS_NO_BACKGROUND, 20, 20, b -> {
                Minecraft.getInstance().setScreen(SkinmaticsGui.openScreen(Minecraft.getInstance().screen));
            });
            button.focusable = false;

            if (!avatarName.isBlank()) {
                button.setTooltip(Tooltip.create(Component.translatable("skinmatics.selected_profile", avatarName)));
            }

            layout.addToFooter(button);
            skinmatics$skinmaticsButton = button;
        }
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    public void skinmatics$repositionSkinmatics(CallbackInfo ci) {
        if (skinmatics$skinmaticsButton != null) {
            skinmatics$skinmaticsButton.setPosition(skinmatics$skinmaticsButton.getX() + 116, skinmatics$skinmaticsButton.getY());
        }
    }
}