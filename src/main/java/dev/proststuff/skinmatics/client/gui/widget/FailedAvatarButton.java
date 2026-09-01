package dev.proststuff.skinmatics.client.gui.widget;

import dev.proststuff.skinmatics.client.gui.SkinmaticsGuiAssets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

public class FailedAvatarButton extends ClickableButton {
    protected String error;

    public FailedAvatarButton(int x, int y, int width, int height, String name, String error) {
        super(x, y, width, height, Component.literal(name).withStyle(ChatFormatting.RED), FailedAvatarButton::onClick);
        this.focusable = false;
        this.error = error;

        setTooltip(Tooltip.create(Component.translatable("skinmatics.avatar.error",error)));
        setTooltipDelay(Duration.ZERO);
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int centerX = getX() + getWidth() / 2;
        Font font = minecraft().font;
        graphics.centeredText(font, getMessage(), centerX, getY() + 8, foregroundColor());
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SkinmaticsGuiAssets.ERROR,
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

    public static void onClick(ClickableButton widget) {}
}
