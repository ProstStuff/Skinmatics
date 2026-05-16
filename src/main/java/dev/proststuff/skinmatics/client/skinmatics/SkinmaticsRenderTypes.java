package dev.proststuff.skinmatics.client.skinmatics;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class SkinmaticsRenderTypes {
    public static final Function<Identifier, RenderType> EYES_NO_CULL;

    public static RenderType eyesNoCull(Identifier texture) {
        return EYES_NO_CULL.apply(texture);
    }

    static {
        EYES_NO_CULL = Util.memoize(texture ->
                RenderType.create("eyes_no_cull", RenderSetup.builder(SkinmaticsRenderPipelines.EYES_NO_CULL)
                        .withTexture("Sampler0", texture)
                        .sortOnUpload()
                        .createRenderSetup())
        );
    }
}
