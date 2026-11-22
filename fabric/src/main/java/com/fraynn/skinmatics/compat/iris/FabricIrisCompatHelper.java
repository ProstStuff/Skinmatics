package com.fraynn.skinmatics.compat.iris;

import com.fraynn.skinmatics.texture.SkinmaticsDynamicTexture;
import net.irisshaders.iris.pbr.TextureTracker;
import net.irisshaders.iris.pbr.loader.PBRTextureLoaderRegistry;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

public class FabricIrisCompatHelper implements IIrisCompatHelper {
    @Override
    public void registerPBRLoader() {
        PBRTextureLoaderRegistry.INSTANCE.register(
                SkinmaticsDynamicTexture.class,
                new SkinmaticsPBRTextureLoader()
        );
    }

    @Override
    public void trackTexture(Identifier id, AbstractTexture texture) {
        TextureTracker.INSTANCE.trackTexture(texture.getTexture().iris$getGlId(), texture);
    }
}
