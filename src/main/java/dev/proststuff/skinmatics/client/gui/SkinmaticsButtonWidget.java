package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public abstract class SkinmaticsButtonWidget extends ButtonWidget {
    protected final SkinmaticsConfig config = SkinmaticsClient.CONFIG;
    protected int backgroundColor = config.getBackgroundColor();
    protected int foregroundColor = config.getForegroundColor();

    protected boolean toggled = false;

    protected SkinmaticsButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    public void toggle() {
        this.toggled = !this.toggled;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    public void setForegroundColor(int color) {
        this.foregroundColor = color;
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int color = getBackgroundColor();

        if (!isMouseOver(mouseX, mouseY) && !toggled) {
            color = ColorHelper.withAlpha(ColorHelper.getAlpha(color) / 2, color);
        }

        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
    }

    public static class IconSkinmaticsButtonWidget extends SkinmaticsButtonWidget {
        protected Identifier icon;

        protected IconSkinmaticsButtonWidget(int x, int y, Identifier icon, int iconWidth, int iconHeight, net.minecraft.text.Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
            super(x, y, iconWidth, iconHeight, message, onPress, narrationSupplier);
            this.icon = icon;
        }

        public void setIcon(Identifier icon, int width, int height) {
            this.icon = icon;
            this.width = width;
            this.height = height;
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.drawIcon(context, mouseX, mouseY, deltaTicks);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, icon, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight(), ColorHelper.withBrightness(foregroundColor, toggled ? 1.0f : 0.5f));
        }
    }

    public static class TextSkinmaticsButtonWidget extends SkinmaticsButtonWidget {
        protected TextSkinmaticsButtonWidget(int x, int y, int width, int height, net.minecraft.text.Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
            super(x, y, width, height, message, onPress, narrationSupplier);
        }

        @Override
        protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.drawIcon(context, mouseX, mouseY, deltaTicks);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            net.minecraft.text.Text text = getMessage();
            int width = textRenderer.getWidth(text);

            context.drawText(textRenderer, text, (getX() + getWidth() / 2) - width / 2, (getY() + getHeight() / 2) - textRenderer.fontHeight / 2, getForegroundColor(), true);
        }
    }

    public static SkinmaticsButtonWidget createText(int x, int y, net.minecraft.text.Text text, int width, int height, PressAction onPress) {
        return new TextSkinmaticsButtonWidget(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public static SkinmaticsButtonWidget createIcon(int x, int y, Identifier icon, int width, int height, net.minecraft.text.Text text, PressAction onPress) {
        return new IconSkinmaticsButtonWidget(x, y, icon, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
    }
}
