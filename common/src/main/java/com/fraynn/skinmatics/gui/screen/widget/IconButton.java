package com.fraynn.skinmatics.gui.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class IconButton extends ClickableButton {
    protected Identifier texture;
    protected int textureWidth;
    protected int textureHeight;

    public IconButton(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight, OnClick onClick) {
        super(x, y, width, height, Component.translatable(texture.toLanguageKey()), onClick);
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public IconButton(int x, int y, Identifier texture, int textureWidth, int textureHeight, OnClick onClick) {
        this(x, y, textureWidth, textureHeight, texture, textureWidth, textureHeight, onClick);
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

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
    }

    public void setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
    }

    @Override
    protected void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                getX() + getWidth() / 2 - textureWidth / 2,
                getY() + getHeight() / 2 - textureHeight / 2,
                0.0F,
                0.0F,
                getWidth(),
                getHeight(),
                textureWidth,
                textureHeight,
                textureWidth,
                textureHeight,
                0xFFFFFFFF
        );
    }
}
