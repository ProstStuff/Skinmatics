package dev.proststuff.skinmatics.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class IconButton extends SimpleBackgroundButton<IconButton> {
    protected final Identifier texture;
    protected final int textureWidth;
    protected final int textureHeight;

    public IconButton(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight, OnPress<IconButton> onPress) {
        super(x, y, width, height, Component.translatable(texture.toLanguageKey()), onPress);
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public static IconButton powerOf2(int x, int y, Identifier texture, int power, OnPress<IconButton> onPress) {
        int size = Math.powExact(2, power);
        return new IconButton(x, y, size, size, texture, size, size, onPress);
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    @Override
    public void renderContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int spaceX = (getWidth() - textureWidth) / 2;
        int spaceY = (getHeight() - textureHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + spaceX, getY() + spaceY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight, getContentColor());
    }
}
