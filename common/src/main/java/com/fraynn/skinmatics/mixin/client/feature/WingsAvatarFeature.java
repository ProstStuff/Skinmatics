package com.fraynn.skinmatics.mixin.client.feature;

import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.AvatarUtils;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.Wings;
import com.fraynn.skinmatics.texture.AvatarTextureData;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WingsLayer.class)
public class WingsAvatarFeature {
    @Shadow
    @Final
    private ElytraModel elytraBabyModel;

    @Shadow
    @Final
    private ElytraModel elytraModel;

    @Inject(method = "getPlayerElytraTexture", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$switchTexture(HumanoidRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof AvatarRenderState renderState) {
            AvatarTracker tracker = ((AvatarTrackedEntity) renderState).skinmatics$getTracker();

            if (tracker == null) return;
            AvatarFeatureData<Wings> featureData = tracker.getFeature(AvatarFeatureTypes.WINGS);
            if (featureData == null || tracker.isHidden(AvatarFeatureTypes.WINGS)) return;
            AvatarTextureData textureData = featureData.settings().baseTextures().get(tracker);
            if (!textureData.isEmpty()) cir.setReturnValue(textureData.id());
        }
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"))
    public void skinmatics$renderEmissive(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (state instanceof AvatarRenderState renderState && ((AvatarTrackedEntity) renderState).skinmatics$getTracker() instanceof AvatarTracker tracker) {
            AvatarFeatureData<Wings> featureData = tracker.getFeature(AvatarFeatureTypes.WINGS);
            if (featureData == null || tracker.isHidden(AvatarFeatureTypes.WINGS)) return;
            Wings settings = featureData.settings();
            if (!settings.emissive()) return;
            AvatarTextureData textureData = settings.emissiveTextures().get(tracker);

            if (!textureData.isEmpty()) {
                ElytraModel model = state.isBaby ? elytraBabyModel : elytraModel;

                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, 0.0078125);
                submitNodeCollector.order(0).submitModel(model, state, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), settings.strongerEmissive()), lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, -0.0078125);
                submitNodeCollector.order(0).submitModel(model, state, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), settings.strongerEmissive()), lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
                poseStack.popPose();
            }
        }
    }
}