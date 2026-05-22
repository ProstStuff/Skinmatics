package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.Profile;
import dev.proststuff.skinmatics.api.impl.PlayerModelGetter;
import dev.proststuff.skinmatics.api.impl.ProfileHolder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
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

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    public void skinmatics$changePlayerModel(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        LivingEntityRenderer<?, ?, ?> livingEntityRenderer = (LivingEntityRenderer<?, ?, ?>) (Object) this;

        if (livingEntityRenderer instanceof ProfileHolder dataHolder && livingEntityRenderer instanceof PlayerModelGetter modelHolder) {
            Profile profile = dataHolder.skinmatics$getProfile();

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
