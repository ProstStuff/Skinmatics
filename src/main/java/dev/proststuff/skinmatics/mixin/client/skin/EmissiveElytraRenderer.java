package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.skinmatics.impl.SkinmaticsProfileHolder;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.equipment.ElytraModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public class EmissiveElytraRenderer {

    @Shadow
    @Final
    private EquipmentAssetManager equipmentAssets;

    @Inject(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "TAIL"))
    public <S> void skinmatics$addEmissiveElytra(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId, Model<? super S> model, S state, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, @Nullable Identifier playerTextureOverride, int outlineColor, int order, CallbackInfo ci) {
        if (state instanceof AvatarRenderState avatarRenderState && model instanceof ElytraModel elytraModel) {
            SkinmaticsProfileHolder dataHolder = (SkinmaticsProfileHolder) state;
            Profile profile = dataHolder.skinmatics$getProfile();

            if (profile != null) {
                Identifier texture = profile.data.getElytra(true);
                if (texture != null) {
                    RenderType renderType = profile.data.getEmissiveRenderType(texture);

                    poseStack.pushPose();
                    poseStack.translate(0.0, 0.0, 0.0025);
                    submitNodeCollector.order(2 + order + equipmentAssets.get(equipmentAssetId).getLayers(layerType).size()).submitModel(elytraModel, avatarRenderState, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, -1, null, outlineColor, null);
                    poseStack.popPose();

                    poseStack.pushPose();
                    poseStack.translate(0.0, 0.0, -0.0025);
                    submitNodeCollector.order(2 + order + equipmentAssets.get(equipmentAssetId).getLayers(layerType).size()).submitModel(elytraModel, avatarRenderState, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, -1, null, outlineColor, null);
                    poseStack.popPose();
                }
            }
        }
    }
}
