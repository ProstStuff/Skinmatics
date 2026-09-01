package dev.proststuff.skinmatics.client.skinmatics.avatar.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Supplier;

public class SkinmaticsDynamicTexture extends DynamicTexture {
    public SkinmaticsDynamicTexture(Supplier<String> label, NativeImage image) {
        super(label, image);
    }
}
