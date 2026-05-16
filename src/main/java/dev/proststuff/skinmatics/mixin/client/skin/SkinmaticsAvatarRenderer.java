package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.skinmatics.*;
import dev.proststuff.skinmatics.client.skinmatics.impl.PlayerModelHolder;
import dev.proststuff.skinmatics.client.skinmatics.impl.SkinmaticsProfileHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public class SkinmaticsAvatarRenderer implements SkinmaticsProfileHolder, PlayerModelHolder {

    @Unique
    protected Profile skinmatics$profile;
    @Unique protected PlayerModel skinmatics$wideModel;
    @Unique protected PlayerModel skinmatics$slimModel;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void skinmatics$retrievePlayerModel(EntityRendererProvider.Context context, boolean slimSteve, CallbackInfo ci) {
        AvatarRenderer<?> avatarRenderer = (AvatarRenderer<?>) (Object) this;

        if (slimSteve) {
            this.skinmatics$slimModel = avatarRenderer.getModel();
            this.skinmatics$wideModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        } else {
            this.skinmatics$wideModel = avatarRenderer.getModel();
            this.skinmatics$slimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <A extends Avatar & ClientAvatarEntity> void skinmatics$extractSkinmaticsData(A entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        SkinmaticsProfileHolder dataHolder = (SkinmaticsProfileHolder) state;
        Profile profile = ProfileHandler.getAssignedProfile(entity);

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && entity.is(localPlayer)) {
            profile = ProfileHandler.getCurrentProfile();
        }

        dataHolder.skinmatics$setProfile(profile);
        skinmatics$setProfile(profile);
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("RETURN"), cancellable = true)
    public void skinmatics$switchSkinTexture(AvatarRenderState state, CallbackInfoReturnable<Identifier> cir) {
        SkinmaticsProfileHolder dataHolder = (SkinmaticsProfileHolder) state;
        Profile profile = dataHolder.skinmatics$getProfile();

        if (profile != null) {
            Identifier texture = profile.data.getSkin(false);
            if (texture != null) cir.setReturnValue(texture);
        }
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        ProfileData data = skinmatics$profile != null ? skinmatics$profile.data : ProfileHandler.getCurrentProfileData();

        if (data != null) {
            Identifier texture = data.getSkin(false);

            if (texture != null) {
                return RenderTypes.entityTranslucent(texture);
            }
        }

        return defaultRenderType;
    }

    @Inject(method = "renderHand", at = @At("TAIL"))
    public void skinmatics$renderEmissiveHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, net.minecraft.client.model.geom.ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        ProfileData data = skinmatics$profile != null ? skinmatics$profile.data : ProfileHandler.getCurrentProfileData();

        if (data != null) {
            Identifier texture = data.getSkin(true);

            if (texture != null) {
                submitNodeCollector.order(1).submitModelPart(arm, poseStack, data.getEmissiveRenderType(texture), lightCoords, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, null);
            }
        }
    }

    @Override
    public void skinmatics$setProfile(Profile profile) {
        this.skinmatics$profile = profile;
    }

    @Override
    public Profile skinmatics$getProfile() {
        return skinmatics$profile;
    }

    @Override
    public PlayerModel skinmatics$getWide() {
        return skinmatics$wideModel;
    }

    @Override
    public PlayerModel skinmatics$getSlim() {
        return skinmatics$slimModel;
    }
}