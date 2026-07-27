package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
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
public class SkinmaticsWings {
    @Shadow
    @Final
    private ElytraModel elytraBabyModel;

    @Shadow
    @Final
    private ElytraModel elytraModel;

    @Inject(method = "getPlayerElytraTexture", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$modifyDefaultElytraTexture(HumanoidRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof AvatarRenderState renderState) {
            Profile profile = renderState.skinmatics$getProfile();
            TextureData textureData = profile != null ? profile.wings().getTextures().get(profile, state) : null;
            if (textureData != null && !textureData.isEmpty()) cir.setReturnValue(textureData.id());
        }
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V"))
    public void skinmatics$renderEmissiveLayer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (state instanceof AvatarRenderState avatarRenderState && avatarRenderState.skinmatics$getTracker() != null) {
            ProfileTracker tracker = avatarRenderState.skinmatics$getTracker();
            Profile profile = tracker.getProfile();
            TextureData textureData = profile.wings().getEmissiveTextures().get(profile, avatarRenderState);

            if (!textureData.isEmpty()) {
                ElytraModel model = state.isBaby ? elytraBabyModel : elytraModel;

                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, 0.0078125);
                submitNodeCollector.order(0).submitModel(model, state, poseStack, profile.getEmissiveRenderType(textureData.id()), lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, -0.0078125);
                submitNodeCollector.order(0).submitModel(model, state, poseStack, profile.getEmissiveRenderType(textureData.id()), lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
                poseStack.popPose();
            }
        }
    }
}
