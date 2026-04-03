package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsDataHolderImpl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO:
// - Able to change slim & wide model anytime
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin implements SkinmaticsDataHolderImpl {
    @Unique
    protected SkinmaticsData skinmatics$skinmaticsData;

    @Inject(method = "getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"), cancellable = true)
    public void skinmatics$modifyDefaultSkinTexture(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        SkinmaticsData data = SkinmaticsClient.getSkinmaticsFromRenderState(playerEntityRenderState);

        if (data != null) {
            skinmatic$setSkinmaticsData(data);

            if (data.validateSkin()) {
                cir.setReturnValue(data.getSkinTexture());
            }
        }
    }

    @ModifyArg(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;)V"), index = 2)
    public RenderLayer skinmatics$modifyDefaultArmTexture(RenderLayer defaultRenderLayer) {
        if (skinmatics$skinmaticsData != null && skinmatics$skinmaticsData.validateSkin() && skinmatics$skinmaticsData.getSkinTexture() != null) {
            return RenderLayers.entityTranslucent(skinmatics$skinmaticsData.getSkinTexture());
        }

        return defaultRenderLayer;
    }

    @Inject(method = "renderArm", at = @At("TAIL"))
    public void skinmatics$renderEmissiveArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible, CallbackInfo ci) {
        if (skinmatics$skinmaticsData != null && skinmatics$skinmaticsData.validateEmissiveSkin() && skinmatics$skinmaticsData.getEmissiveSkinTexture() != null) {
            queue.getBatchingQueue(1).submitModelPart(arm, matrices, skinmatics$skinmaticsData.getEmissiveRenderLayer(skinmatics$skinmaticsData.getEmissiveSkinTexture()), light, OverlayTexture.DEFAULT_UV, null);
        }
    }

    @Override
    public void skinmatic$setSkinmaticsData(SkinmaticsData skinmaticsData) {
        this.skinmatics$skinmaticsData = skinmaticsData;
    }
}