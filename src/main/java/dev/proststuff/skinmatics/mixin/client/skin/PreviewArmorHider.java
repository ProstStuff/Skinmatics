package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.impl.AvatarPreviewState;
import dev.proststuff.skinmatics.client.gui.element.avatar.BodyPart;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarMinimap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class PreviewArmorHider {
    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    public void skinmatics$cancelArmorForPreview(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, HumanoidRenderState state, CallbackInfo ci) {
        if (state instanceof AvatarPreviewState avatarPreviewer) {
            if (avatarPreviewer.skinmatics$isPreviewing()) {
                for (BodyPart part : BodyPart.values()) {
                    if (part.is(slot) && !AvatarMinimap.isVisible(part)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
