package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.mixin.PlayerModelGetter;
import dev.proststuff.skinmatics.api.client.impl.mixin.PlayerModelSetter;
import dev.proststuff.skinmatics.api.client.impl.mixin.ProfileHolder;
import dev.proststuff.skinmatics.api.client.impl.mixin.UUIDHolder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
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
        if (state instanceof ProfileHolder holder && state instanceof PlayerModelGetter modelHolder && state instanceof UUIDHolder uuidHolder && model instanceof PlayerModel) {
            Profile profile = holder.skinmatics$getProfile();

            if (uuidHolder.skinmatics$getUUID() == ProfileManager.getLocalUUID()) {
                profile = ProfileManager.getCurrent();
            }

            if (profile != null) {
                skinmatics$setCurrent(modelHolder.skinmatics$getModel(profile));
            }
        }
    }

    @Override
    public void skinmatics$setWide(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setSlim(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setSlimArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setWideArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setCurrentArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setCurrent(PlayerModel model) {
        this.model = model != null ? model : this.model;
    }
}