package dev.proststuff.skinmatics.client.gui.widget.button;

import dev.proststuff.skinmatics.client.gui.screen.CreationScreen;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class NewProfileButton extends SimpleBackgroundButton<NewProfileButton> {
    public NewProfileButton(int x, int y, int width, int height) {
        super(x, y, width, height, Component.translatable("skinmatics.new_profile"), (_, _) -> Minecraft.getInstance().setScreen(new CreationScreen(Minecraft.getInstance().screen)));
    }

    @Override
    public void renderContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.centeredText(font(), getMessage(), getX() + getWidth() / 2, getY() + SPACING, getContentColor());
        graphics.blit(RenderPipelines.GUI_TEXTURED, SkinmaticsAssets.NEW_PROFILE, getX() + getWidth() / 2 - 16, getY() + getHeight() / 2 - 16, 0, 0, 32, 32, 32, 32, getContentColor());
    }
}
