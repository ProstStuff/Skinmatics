package dev.proststuff.skinmatics.compat.iris;

import dev.proststuff.skinmatics.avatar.v1.texture.SkinmaticsDynamicTexture;
import net.irisshaders.iris.pbr.TextureTracker;
import net.irisshaders.iris.pbr.loader.PBRTextureLoaderRegistry;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class IrisCompat {
    public static final Map<AbstractTexture, Identifier> TEXTURES = new HashMap<>();

    public static void init() {
        PBRTextureLoaderRegistry.INSTANCE.register(
                SkinmaticsDynamicTexture.class,
                new SkinmaticsPBRTextureLoader()
        );
    }

    public static void trackTexture(Identifier id, AbstractTexture texture) {
        TEXTURES.put(texture, id);
        TextureTracker.INSTANCE.trackTexture(texture.getTexture().iris$getGlId(), texture);
    }
}
