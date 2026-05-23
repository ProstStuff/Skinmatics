package dev.proststuff.skinmatics.mixin.client.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.impl.EntityPreviewer;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarMinimap;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.skinmatics.client.gui.element.avatar.BodyPart;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public class PreviewElytraHider {
    @Inject(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At("HEAD"), cancellable = true)
    public <S> void skinmatics$hideElytra(EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId, Model<? super S> model, S state, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, @Nullable Identifier playerTextureOverride, int outlineColor, int order, CallbackInfo ci) {
        if (state instanceof EntityPreviewer entityPreviewer && itemStack.is(Items.ELYTRA)) {
            if (entityPreviewer.skinmatics$isPreviewing() && AvatarPreviewRenderer.PREVIEWER != null) {
                if (!AvatarMinimap.isVisible(BodyPart.TORSO)) {
                    ci.cancel();
                }
            }
        }
    }
}
