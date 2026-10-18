package dev.proststuff.skinmatics.mixin.client.feature;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.Cape;
import dev.proststuff.skinmatics.avatar.texture.AvatarTextureData;
import dev.proststuff.utilitary.client.texture.DummyTextureAsset;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.ClientAsset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeAvatarFeature {
    @Shadow
    @Final
    private HumanoidModel<AvatarRenderState> model;

    /*
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void skinmatics$render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker == null) return;
        AvatarFeatureData<Cape> featureData = tracker.getFeature(AvatarFeatureTypes.CAPE);
        if (featureData == null) return;
        Cape settings = featureData.settings();

        if (settings.visible().get()) ci.cancel();
    }

    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;showCape:Z", opcode = Opcodes.GETFIELD))
    public boolean skinmatics$changeCapeVisibility(boolean original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker == null) return original;
        AvatarFeatureData<Cape> featureData = tracker.getFeature(AvatarFeatureTypes.CAPE);
        if (featureData == null) return original;
        Cape settings = featureData.settings();
        if (settings.visible().get()) return true;

        return original;
    }*/

    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;cape()Lnet/minecraft/core/ClientAsset$Texture;"))
    public ClientAsset.Texture skinmatics$fakeDummyCape(ClientAsset.Texture original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (original != null || tracker == null) return original;
        AvatarFeatureData<Cape> featureData = tracker.getFeature(AvatarFeatureTypes.CAPE);
        if (featureData == null) return null;

        Cape settings = featureData.settings();
        return settings.customBaseTexture() && !settings.baseTextures().isEmpty() ? DummyTextureAsset.INSTANCE : null;
    }

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private void skinmatics$renderCustom(SubmitNodeCollector collector, Model model, Object stateObj, PoseStack _poseStack, RenderType renderType, int light, int overlay, int color, ModelFeatureRenderer.CrumblingOverlay overlayData, Operation<Void> original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker != null) {
            AvatarFeatureData<Cape> featureData = tracker.getFeature(AvatarFeatureTypes.CAPE);

            if (featureData != null) {
                Cape settings = featureData.settings();
                AvatarTextureData textureData = settings.baseTextures().get(tracker);

                if (!textureData.isEmpty()) renderType = RenderTypes.entityTranslucent(textureData.id());
            }
        }

        original.call(collector, model, stateObj, poseStack, renderType, light, overlay, color, overlayData);
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    public void skinmatics$renderEmissive(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker == null) return;
        AvatarFeatureData<Cape> featureData = tracker.getFeature(AvatarFeatureTypes.CAPE);
        if (featureData == null || !featureData.settings().emissive()) return;
        AvatarTextureData textureData = featureData.settings().emissiveTextures().get(tracker);
        if (!textureData.isEmpty()) submitNodeCollector.order(1).submitModel(model, state, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), featureData.settings().strongerEmissive()), lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }
}