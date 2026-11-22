package com.fraynn.skinmatics.compat.iris;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class IrisCompat {
    public static final Map<AbstractTexture, Identifier> TEXTURES = new HashMap<>();

    public static void trackTexture(Identifier id, AbstractTexture texture) {
        TEXTURES.put(texture, id);
        IIrisCompatHelper.INSTANCE.getOrThrow().trackTexture(id, texture);
    }
}
