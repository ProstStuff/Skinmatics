package dev.proststuff.skinmatics.mixin.client.skin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsDataHolderImpl;
import dev.proststuff.skinmatics.client.utility.DummyTextureAsset;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.AssetInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin implements SkinmaticsDataHolderImpl {
    @Shadow
    @Final
    private BipedEntityModel<PlayerEntityRenderState> model;
    @Unique
    SkinmaticsData skinmatics$skinmaticsData;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void skinmatics$render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g, CallbackInfo ci) {
        SkinmaticsData skinmaticsData = SkinmaticsClient.getSkinmaticsFromRenderState(playerEntityRenderState);

        if (skinmaticsData != null) {
            skinmatic$setSkinmaticsData(skinmaticsData);
            if (!skinmaticsData.capeEnabled) ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;capeVisible:Z"))
    public boolean skinmatics$modifyCapeVisibility(boolean original) {
        if (skinmatics$skinmaticsData != null && skinmatics$skinmaticsData.showCape()) return true;
        return original;
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/SkinTextures;cape()Lnet/minecraft/util/AssetInfo$TextureAsset;"))
    public AssetInfo.TextureAsset skinmatics$returnDummy(AssetInfo.TextureAsset original) {
        return original == null && skinmatics$skinmaticsData.validateCustomCape() ? DummyTextureAsset.INSTANCE : original;
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"), index = 3)
    public RenderLayer skinmatics$overrideDefaultCapeTexture(RenderLayer defaultLayer) {
        if (skinmatics$skinmaticsData != null && skinmatics$skinmaticsData.validateCustomCape() && skinmatics$skinmaticsData.getCapeTexture() != null) {
            return RenderLayers.entityTranslucent(skinmatics$skinmaticsData.getCapeTexture());
        }

        return defaultLayer;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"))
    public void skinmatics$renderEmissive(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g, CallbackInfo ci) {
        if (skinmatics$skinmaticsData != null && skinmatics$skinmaticsData.validateEmissiveCape() && skinmatics$skinmaticsData.getEmissiveCapeTexture() != null) {
            orderedRenderCommandQueue.getBatchingQueue(1).submitModel(model, playerEntityRenderState, matrixStack, skinmatics$skinmaticsData.getEmissiveRenderLayer(skinmatics$skinmaticsData.getEmissiveCapeTexture()), i, OverlayTexture.DEFAULT_UV, playerEntityRenderState.outlineColor, null);
        }
    }

    @Override
    public void skinmatic$setSkinmaticsData(SkinmaticsData skinmaticsData) {
        this.skinmatics$skinmaticsData = skinmaticsData;
    }
}
