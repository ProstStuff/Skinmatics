package dev.proststuff.skinmatics.mixin.client.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.Body;
import dev.proststuff.skinmatics.avatar.texture.AvatarTextureData;
import dev.proststuff.skinmatics.entity.AvatarPlayer;
import dev.proststuff.skinmatics.avatar.manager.AvatarTrackerManager;
import dev.proststuff.skinmatics.util.SkinmaticsGraphicsUtils;
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
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker == null) return;

        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);

        if (avatarFeatureData != null) {
            Body settings = avatarFeatureData.settings();
            AvatarTextureData textureData = settings.baseTextures().get(tracker);

            if (!textureData.isEmpty()) {
                cir.setReturnValue(textureData.id());
                return;
            }
        }

        if (AvatarTracker.getEntityClient(state.utilitary$getUUID()) instanceof AvatarPlayer) {
            Identifier texture = SkinmaticsGraphicsUtils.getPlayerSkin(Minecraft.getInstance().getGameProfile());
            if (texture == null) return;
            cir.setReturnValue(texture);
        }
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(AvatarUtils.getClientUUID());

        if (tracker == null) return defaultRenderType;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getFeature(AvatarFeatureTypes.BODY);
        if (avatarFeatureData == null) return defaultRenderType;
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
        if (avatarFeatureData == null) return;
        Body settings = avatarFeatureData.settings();
        AvatarTextureData textureData = settings.emissiveTextures().get(tracker);

        if (!textureData.isEmpty()) submitNodeCollector.order(1).submitModelPart(arm, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), settings.strongerEmissive()), lightCoords, OverlayTexture.NO_OVERLAY, null);
    }
}