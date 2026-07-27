package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.TrackedProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileUtils;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
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
    public <A extends Avatar & ClientAvatarEntity> void skinmatics$extractSkinmatics(A entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        ProfileTracker entityTracker = TrackedProfileManager.get(entity);

        if (entityTracker != null) {
            state.skinmatics$setTracker(entityTracker);
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    public void skinmatics$switchSkinTexture(AvatarRenderState state, CallbackInfoReturnable<Identifier> cir) {
        Profile profile = state.skinmatics$getProfile();

        if (profile != null) {
            TextureData textureData = profile.skin().getTextures().get(profile, state);
            if (!textureData.isEmpty()) cir.setReturnValue(textureData.id());
        }
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        Entity entity = ProfileUtils.getViewingEntity();
        ProfileTracker entityTracker = TrackedProfileManager.getViewing();

        if (entity != null && entityTracker != null) {
            Profile profile = entityTracker.getProfile();
            TextureData textureData = profile.skin().getTextures().get(entity.tickCount, profile.maxTicks());
            if (!textureData.isEmpty()) return RenderTypes.entityTranslucent(textureData.id());
        }

        return defaultRenderType;
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    public void skinmatics$renderEmissiveHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        Entity entity = ProfileUtils.getViewingEntity();
        ProfileTracker entityTracker = TrackedProfileManager.getViewing();

        if (entity != null && entityTracker != null) {
            Profile profile = entityTracker.getProfile();
            TextureData textureData = profile.skin().getEmissiveTextures().get(entity.tickCount, profile.maxTicks());
            if (!textureData.isEmpty()) {
                submitNodeCollector.order(1).submitModelPart(arm, poseStack, profile.getEmissiveRenderType(textureData.id()), lightCoords, OverlayTexture.NO_OVERLAY, null);
            }
        }
    }
}