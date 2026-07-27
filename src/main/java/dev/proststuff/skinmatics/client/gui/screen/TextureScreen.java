package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TextureScreen extends HomeScreen {
    public TextureScreen(Screen parent) {
        super(Component.translatable("skinmatics.textures"), parent);
    }

    @Override
    public Identifier getScreenId() {
        return SkinmaticsAssets.TEXTURES;
    }

    @Override
    public void populateContent() {

    }
}
