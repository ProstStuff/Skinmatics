package dev.proststuff.skinmatics.client.gui.widget;

import dev.proststuff.skinmatics.client.gui.NewAvatarScreen;
import dev.proststuff.skinmatics.client.gui.SkinmaticsGuiAssets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class NewAvatarButton extends ClickableButton {
    public NewAvatarButton(int x, int y, int width, int height) {
        super(x, y, width, height, Component.translatable("skinmatics.avatar.new"), NewAvatarButton::onClick);
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int centerX = getX() + getWidth() / 2;
        Font font = minecraft().font;
        graphics.centeredText(font, getMessage(), centerX, getY() + 8, foregroundColor());
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SkinmaticsGuiAssets.NEW_AVATAR,
                centerX - 16,
                getY() + getHeight() / 2 - 16,
                0.0F,
                0.0F,
                32,
                32,
                32,
                32,
                32,
                32,
                0xFFFFFFFF
        );
    }

    public static void onClick(ClickableButton widget) {
        Minecraft.getInstance().setScreen(new NewAvatarScreen(widget.getParent()));
    }
}
