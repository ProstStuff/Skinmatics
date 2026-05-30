package dev.proststuff.skinmatics.mixin.client.skin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileData;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.mixin.*;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
public class SkinmaticsAvatarRenderer implements ProfileHolder, PlayerModelGetter {
    @Unique protected Profile skinmatics$profile;
    @Unique protected PlayerModel skinmatics$wideModel;
    @Unique protected PlayerModel skinmatics$slimModel;
    @Unique protected ArmorModelSet<PlayerModel> skinmatics$wideArmor;
    @Unique protected ArmorModelSet<PlayerModel> skinmatics$slimArmor;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void skinmatics$retrievePlayerModel(EntityRendererProvider.Context context, boolean slimSteve, CallbackInfo ci) {
        this.skinmatics$wideModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        this.skinmatics$slimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.skinmatics$wideArmor = ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), (part) -> new PlayerModel(part, false));
        this.skinmatics$slimArmor = ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), (part) -> new PlayerModel(part, true));

        Profile profile = ProfileManager.getCurrent();

        if (profile != null) {
            ((PlayerModelSetter) this).skinmatics$setCurrent(skinmatics$getModel(profile));
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <A extends Avatar & ClientAvatarEntity> void skinmatics$extractSkinmaticsData(A entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        state.skinmatics$setUUID(entity.getUUID());

        if (entity instanceof EntityPreviewer previewer && previewer.skinmatics$isPreviewing()) {
            state.skinmatics$preview();
        }

        Profile profile;
        if (ProfileManager.isLocalPlayer(entity)) {
            profile = ProfileManager.getCurrent();
        } else {
            profile = ProfileManager.getAssigned(entity);
        }

        state.skinmatics$setWide(skinmatics$wideModel);
        state.skinmatics$setSlim(skinmatics$slimModel);
        state.skinmatics$setWideArmor(skinmatics$wideArmor);
        state.skinmatics$setSlimArmor(skinmatics$slimArmor);

        state.skinmatics$setProfile(profile);
        skinmatics$setProfile(profile);

        if (profile != null) {
            PlayerModelSetter rendererModelSetter = (PlayerModelSetter) this;
            rendererModelSetter.skinmatics$setCurrent((state.skinmatics$getModel(profile)));
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("RETURN"), cancellable = true)
    public void skinmatics$switchSkinTexture(AvatarRenderState state, CallbackInfoReturnable<Identifier> cir) {
        Profile profile = state.skinmatics$getProfile();

        if (profile != null) {
            Identifier texture = profile.data.getSkin(false);
            if (texture != null) cir.setReturnValue(texture);
        }
    }


    @ModifyExpressionValue(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;getModel()Lnet/minecraft/client/model/EntityModel;"))
    public EntityModel skinmatics$swapModel(EntityModel original) {
        if (original instanceof PlayerModel) {
            Profile profile = skinmatics$profile;

            if (profile == null) {
                if (ProfileManager.getCurrent() == null) {
                    profile = ProfileManager.loadCurrent();
                } else {
                    profile = ProfileManager.getCurrent();
                }
            }

            /**
             * BUG:
             * I don't know if it's fixed or not but the arm model render just won't correctly change
             * I've been trying to fix this for weeks
             * Yet their model always on the wrong type
             */
            if (profile != null) {
                return skinmatics$getModel(profile);
            }
        }

        return original;
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"), index = 2)
    public RenderType skinmatics$switchHandTexture(RenderType defaultRenderType) {
        Profile current = ProfileManager.getCurrent();
        ProfileData currentData = current != null ? current.data : null;
        ProfileData data = skinmatics$profile != null ? skinmatics$profile.data : currentData;

        if (data != null) {
            Identifier texture = data.getSkin(false);

            if (texture != null) {
                return RenderTypes.entityTranslucent(texture);
            }
        }

        return defaultRenderType;
    }

    @Inject(method = "renderHand", at = @At("TAIL"))
    public void skinmatics$renderEmissiveHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        Profile current = ProfileManager.getCurrent();
        ProfileData currentData = current != null ? current.data : null;
        ProfileData data = skinmatics$profile != null ? skinmatics$profile.data : currentData;

        if (data != null) {
            Identifier texture = data.getSkin(true);

            if (texture != null) {
                submitNodeCollector.order(1).submitModelPart(arm, poseStack, data.getEmissiveRenderType(texture), lightCoords, OverlayTexture.NO_OVERLAY, null);
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

    @Override
    public ArmorModelSet<PlayerModel> skinmatics$getWideArmor() {
        return skinmatics$wideArmor;
    }

    @Override
    public ArmorModelSet<PlayerModel> skinmatics$getSlimArmor() {
        return skinmatics$slimArmor;
    }
}