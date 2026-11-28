package com.fraynn.skinmatics.mixin.client.ui;

import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import com.fraynn.skinmatics.gui.screen.SkinmaticsGui;
import com.fraynn.skinmatics.gui.screen.SkinmaticsGuiAssets;
import com.fraynn.skinmatics.gui.screen.widget.IconButton;
import com.fraynn.skinmatics.avatar.AvatarProfileManager;
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
            AvatarProfile avatarData = AvatarProfileManager.INSTANCE.get(avatarName);

            if (avatarData != null) {
                avatarName = avatarData.name();
            } else {
                avatarName = "";
            }

            IconButton button = new IconButton(0, 0, 20, 20, SkinmaticsGuiAssets.SKINMATICS_NO_BACKGROUND, 20, 20, b -> {
                Minecraft.getInstance().setScreenAndShow(SkinmaticsGui.openScreen(Minecraft.getInstance().gui.screen()));
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