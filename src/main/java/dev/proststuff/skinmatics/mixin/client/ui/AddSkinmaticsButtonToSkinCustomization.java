package dev.proststuff.skinmatics.mixin.client.ui;

import dev.proststuff.skinmatics.SkinmaticsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SkinCustomizationScreen.class)
public abstract class AddSkinmaticsButtonToSkinCustomization extends OptionsSubScreen {
    public AddSkinmaticsButtonToSkinCustomization(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    public void skinmatics$addOptions(CallbackInfo ci) {
        assert list != null;
        list.addSmall(List.of(Button.builder(Component.translatable("skinmatics.options.skinmatics"), (_ -> Minecraft.getInstance().setScreen(SkinmaticsClient.openCustomizationScreen(this)))).build()));
    }
}
