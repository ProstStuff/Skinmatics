package dev.proststuff.skinmatics.mixin.client.skin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.skinmatics.impl.SkinmaticsProfileHolder;
import dev.proststuff.skinmatics.client.utility.DummyTextureAsset;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeTextureSwapper implements SkinmaticsProfileHolder {
    @Shadow
    @Final
    private HumanoidModel<AvatarRenderState> model;
    @Unique
    Profile skinmatics$profile;

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void skinmatics$render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        SkinmaticsProfileHolder dataHolder = (SkinmaticsProfileHolder) state;
        Profile profile = dataHolder.skinmatics$getProfile();

        if (profile != null) {
            skinmatics$setProfile(profile);
            if (!profile.showCape.get()) ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;showCape:Z", opcode = Opcodes.GETFIELD))
    public boolean skinmatics$changeCapeVisibility(boolean original) {
        if (skinmatics$profile != null && skinmatics$profile.enabled.get() && skinmatics$profile.showCape.get()) return true;
        return original;
    }

    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;cape()Lnet/minecraft/core/ClientAsset$Texture;"))
    public ClientAsset.Texture skinmatics$dummyCape(ClientAsset.Texture original) {
        return original == null && skinmatics$profile != null && skinmatics$profile.data.getCape(false) != null ? DummyTextureAsset.INSTANCE : original;
    }

    @ModifyArg(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"), index = 3)
    public RenderType skinmatics$changeCapeTexture(RenderType defaultRenderType) {
        if (skinmatics$profile != null) {
            Identifier texture = skinmatics$profile.data.getCape(false);
            if (texture != null) {
                return RenderTypes.entityTranslucent(texture);
            }
        }

        return defaultRenderType;
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    public void skinmatics$renderEmissiveCape(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (skinmatics$profile != null) {
            Identifier texture =  skinmatics$profile.data.getCape(true);
            submitNodeCollector.order(1).submitModel(model, state, poseStack, skinmatics$profile.data.getEmissiveRenderType(texture), lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }

    @Override
    public void skinmatics$setProfile(Profile skinmaticsData) {
        this.skinmatics$profile = skinmaticsData;
    }

    @Override
    public Profile skinmatics$getProfile() {
        return skinmatics$profile;
    }
}