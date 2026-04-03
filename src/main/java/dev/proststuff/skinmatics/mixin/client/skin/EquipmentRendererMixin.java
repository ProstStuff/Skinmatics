package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V", at = @At(value = "HEAD"))
    public void skinmatics$addEmissiveElytra(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model<?> model, Object state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, @Nullable Identifier textureId, int outlineColor, int initialOrder, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState playerEntityRenderState && model instanceof ElytraEntityModel elytraEntityModel) {
            SkinmaticsData skinmaticsData = SkinmaticsClient.getSkinmaticsFromRenderState(playerEntityRenderState);

            if (skinmaticsData != null && skinmaticsData.validateEmissiveElytra() && skinmaticsData.getEmissiveElytraTexture() != null) {
                matrices.push();
                matrices.translate(0.0, 0.0, 0.005); // Slight offset to FORCE emissive texture to NOT render behind the real elytra
                queue.getBatchingQueue(1).submitModel(elytraEntityModel, playerEntityRenderState, matrices, skinmaticsData.getEmissiveRenderLayer(skinmaticsData.getEmissiveElytraTexture()), light, OverlayTexture.DEFAULT_UV, -1, null, outlineColor, null);
                matrices.pop();
            }
        }
    }
}
