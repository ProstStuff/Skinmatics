package dev.proststuff.skinmatics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarData;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarTrackerManager;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.client.skinmatics.avatar.texture.AvatarTextureData;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class SkinmaticsAvatar {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <A extends net.minecraft.world.entity.Avatar & ClientAvatarEntity> void skinmatics$extractSkinmatics(A entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);
        state.skinmatics$apply(tracker, entity.isDeadOrDying(), tracker != null && tracker.isBlinking());
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    public void skinmatics$switchSkinTexture(AvatarRenderState state, CallbackInfoReturnable<Identifier> cir) {
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker != null) {
            AvatarTextureData textureData = tracker.getAvatar().skin().getTextures().get(tracker);
            if (!textureData.isEmpty()) cir.setReturnValue(textureData.id());
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    public void skinmatics$patchUntrackedHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        Entity entity = AvatarUtils.getClientEntity();
        if (entity == null || entity.isRemoved()) ci.cancel();
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(AvatarUtils.getClientUUID());

        if (tracker != null) {
            AvatarData avatarData = tracker.getAvatar();
            AvatarTextureData textureData = avatarData.skin().getTextures().get(tracker);
            if (!textureData.isEmpty()) return RenderTypes.entityTranslucent(textureData.id());
        }

        return defaultRenderType;
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    public void skinmatics$renderEmissiveHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(AvatarUtils.getClientUUID());

        if (tracker != null) {
            AvatarData avatarData = tracker.getAvatar();
            AvatarTextureData textureData = avatarData.skin().getEmissiveTextures().get(tracker);

            if (!textureData.isEmpty()) {
                submitNodeCollector.order(1).submitModelPart(arm, poseStack, avatarData.getEmissiveRenderType(textureData.id()), lightCoords, OverlayTexture.NO_OVERLAY, null);
            }
        }
    }
}