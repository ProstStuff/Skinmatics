package dev.proststuff.skinmatics.client.skinmatics;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;

import static net.minecraft.client.renderer.RenderPipelines.MATRICES_FOG_SNIPPET;

public class SkinmaticsRenderPipelines {
    public static final RenderPipeline EYES_NO_CULL = RenderPipelines.register(
            RenderPipeline.builder(MATRICES_FOG_SNIPPET)
                    .withLocation("pipeline/eyes_no_cull")
                    .withVertexShader("core/entity")
                    .withFragmentShader("core/entity")
                    .withShaderDefine("EMISSIVE")
                    .withShaderDefine("NO_OVERLAY")
                    .withShaderDefine("NO_CARDINAL_LIGHTING")
                    .withSampler("Sampler0")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                    .withCull(false)
                    .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
                    .build());
}
