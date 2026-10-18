package dev.proststuff.skinmatics.avatar.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Supplier;

public class SkinmaticsDynamicTexture extends DynamicTexture {
    protected final int width;
    protected final int height;
    protected final NativeImage.Format format;

    public SkinmaticsDynamicTexture(Supplier<String> label, NativeImage image) {
        super(label, image);
        width = image.getWidth();
        height = image.getHeight();
        format = image.format();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
