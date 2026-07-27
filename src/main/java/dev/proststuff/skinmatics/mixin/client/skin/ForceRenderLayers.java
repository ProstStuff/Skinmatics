package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class ForceRenderLayers<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Shadow
    @Final
    protected List<RenderLayer<S, M>> layers;

    @Shadow
    protected abstract boolean shouldRenderLayers(S state);

    @Shadow
    public abstract M getModel();

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z"), cancellable = true)
    public void skinmatics$forceRenderLayer(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (!shouldRenderLayers(state)) {
            boolean animInitialized = false;

            for (RenderLayer<S, M> layer : layers) {
                if (!(layer instanceof ForceRenderLayer forceRenderLayer) || !forceRenderLayer.shouldRenderWhenInvisible()) continue;

                if (!animInitialized) {
                    getModel().setupAnim(state);
                    animInitialized = true;
                }

                layer.submit(poseStack, submitNodeCollector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }
    }
}
