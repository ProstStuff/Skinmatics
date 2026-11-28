package com.fraynn.skinmatics.mixin.client.feature;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import com.fraynn.skinmatics.avatar.AvatarUtils;
import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.Body;
import com.fraynn.skinmatics.entity.AvatarPlayer;
import com.fraynn.skinmatics.gui.SkinmaticsGraphicsUtils;
import com.fraynn.skinmatics.texture.AvatarTextureData;
import com.fraynn.utilitary.client.entity.UUIDRenderStateHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class BodyAvatarFeature {
    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    public void skinmatics$switchBodyTexture(AvatarRenderState state, CallbackInfoReturnable<Identifier> cir) {
        AvatarTracker tracker = ((AvatarTrackedEntity) state).skinmatics$getTracker();
        if (tracker == null) return;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);

        if (avatarFeatureData != null && !tracker.isHidden(AvatarFeatureTypes.BODY)) {
            Body settings = avatarFeatureData.settings();
            AvatarTextureData textureData = settings.baseTextures().get(tracker);

            if (!textureData.isEmpty()) {
                cir.setReturnValue(textureData.id());
                return;
            }
        }

        if (AvatarTracker.getEntityClient(((UUIDRenderStateHolder) state).utilitary$getUUID()) instanceof AvatarPlayer) {
            Identifier texture = SkinmaticsGraphicsUtils.getPlayerSkin(Minecraft.getInstance().getGameProfile());
            if (texture == null) return;
            cir.setReturnValue(texture);
        }
    }

    @Inject(method = "scale(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
    public void skinmatics$scaleBody(AvatarRenderState state, PoseStack poseStack, CallbackInfo ci) {
        AvatarTracker tracker = ((AvatarTrackedEntity) state).skinmatics$getTracker();

        if (tracker == null) return;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);
        if (avatarFeatureData == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return;
        Body settings = avatarFeatureData.settings();
        poseStack.scale(settings.scale().x, settings.scale().y, settings.scale().x);
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(AvatarUtils.getClientUUID());

        if (tracker == null) return defaultRenderType;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);
        if (avatarFeatureData == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return defaultRenderType;
        Body settings = avatarFeatureData.settings();
        AvatarTextureData textureData = settings.baseTextures().get(tracker);
        if (!textureData.isEmpty()) return RenderTypes.entityTranslucent(textureData.id());
        return defaultRenderType;
    }

    @Inject(method = "renderHand", at = @At("TAIL"))
    public void skinmatics$renderEmissiveHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(AvatarUtils.getClientUUID());

        if (tracker == null) return;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);
        if (avatarFeatureData == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return;
        Body settings = avatarFeatureData.settings();
        AvatarTextureData textureData = settings.emissiveTextures().get(tracker);

        if (!textureData.isEmpty()) submitNodeCollector.order(1).submitModelPart(arm, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), settings.strongerEmissive()), lightCoords, OverlayTexture.NO_OVERLAY, null);
    }
}