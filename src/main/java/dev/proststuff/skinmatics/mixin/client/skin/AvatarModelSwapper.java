package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.mixin.PlayerModelSetter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class AvatarModelSwapper implements PlayerModelSetter {
    @Shadow
    protected EntityModel<?> model;

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    public void skinmatics$changePlayerModel(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (state instanceof AvatarRenderState renderState && model instanceof PlayerModel) {
            Profile profile = renderState.skinmatics$getProfile();

            if (renderState.skinmatics$getUUID() == ProfileManager.getLocalUUID()) {
                profile = ProfileManager.getCurrent();
            }

            if (profile != null) {
                skinmatics$setCurrent(renderState.skinmatics$getModel(profile));
            }
        }
    }

    @Override
    public void skinmatics$setCurrent(PlayerModel model) {
        this.model = model != null ? model : this.model;
    }
}