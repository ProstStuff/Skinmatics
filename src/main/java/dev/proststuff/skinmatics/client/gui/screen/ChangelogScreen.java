package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.SkinmaticsMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ChangelogScreen extends AbstractSkinmaticsScreen {
    public ChangelogScreen(Screen parent) {
        super(Component.translatable("skinmatics.changelog", SkinmaticsMod.getVersionString()), parent);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}