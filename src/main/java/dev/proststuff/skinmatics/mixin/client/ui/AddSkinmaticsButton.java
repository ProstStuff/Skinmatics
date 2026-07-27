package dev.proststuff.skinmatics.mixin.client.ui;

import dev.proststuff.skinmatics.client.gui.screen.ProfileScreen;
import dev.proststuff.skinmatics.client.gui.widget.button.IconButton;
import dev.proststuff.skinmatics.client.skinmatics.ProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
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

    @Unique private AbstractButton skinmatics$skinmaticsButton;

    @Inject(method = "addFooter", at = @At("TAIL"))
    public void skinmatics$addSkinmatics(CallbackInfo ci) {
        OptionsSubScreen screen = (OptionsSubScreen) (Object)this;

        if (screen instanceof SkinCustomizationScreen) {
            Minecraft minecraft = Minecraft.getInstance();
            String profileName = SkinmaticsConfig.getInstance().getProfile();
            Profile profile = ProfileManager.getLoaded(profileName);

            if (profile != null) {
                profileName = profile.name();
            } else {
                profileName = ProfileUtils.toProfilePath(profileName, true);
            }

            IconButton button = new IconButton(0, 0, 20, 20, SkinmaticsAssets.SKINMATICS_NO_BACKGROUND, 16, 16, (_, _) -> minecraft.setScreen(new ProfileScreen(screen)));
            button.setTooltip(Tooltip.create(Component.translatable("skinmatics.selected_profile", profileName)));
            button.setOverrideFocus(true, false);

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
