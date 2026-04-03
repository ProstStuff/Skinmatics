package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$modifyDefaultElytraTexture(BipedEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
            SkinmaticsData skinmaticsData = SkinmaticsClient.getSkinmaticsFromRenderState(playerEntityRenderState);

            if (skinmaticsData != null && skinmaticsData.validateElytra()) {
                cir.setReturnValue(skinmaticsData.getElytraTexture());
            }
        }
    }
}
