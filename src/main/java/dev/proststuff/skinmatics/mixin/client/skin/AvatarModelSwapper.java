package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.PlayerModelGetter;
import dev.proststuff.skinmatics.api.client.impl.ProfileHolder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class AvatarModelSwapper {
    @Shadow
    protected EntityModel<?> model;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void skinmatics$changeDefaultPlayerModel(EntityRendererProvider.Context context, EntityModel model, float shadow, CallbackInfo ci) {
        if (this instanceof ProfileHolder holder && this instanceof PlayerModelGetter modelHolder && model instanceof PlayerModel) {
            Profile profile = holder.skinmatics$getProfile();

            if (profile == null) {
                profile = ProfileManager.getCurrent();
            }

            if (profile != null) {
                if (profile.slim.get()) {
                    this.model = modelHolder.skinmatics$getSlim();
                } else {
                    this.model = modelHolder.skinmatics$getWide();
                }
            }
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    public void skinmatics$changePlayerModel(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (model instanceof PlayerModel && state instanceof ProfileHolder holder && state instanceof PlayerModelGetter modelHolder) {
            Profile profile = holder.skinmatics$getProfile();

            if (profile == null) {
                profile = ProfileManager.getCurrent();
            }

            if (profile != null) {
                if (profile.slim.get()) {
                    this.model = modelHolder.skinmatics$getSlim();
                } else {
                    this.model = modelHolder.skinmatics$getWide();
                }
            }
        }
    }
}
