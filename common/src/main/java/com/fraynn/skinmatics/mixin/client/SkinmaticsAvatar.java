package com.fraynn.skinmatics.mixin.client;

import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.fraynn.skinmatics.avatar.AvatarUtils;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class SkinmaticsAvatar {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <A extends Avatar & ClientAvatarEntity> void skinmatics$extract(A entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        ((AvatarTrackedEntity) state).skinmatics$setTracker(AvatarTrackerManager.INSTANCE.get(entity));
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    public void skinmatics$patchUntrackedHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        Entity entity = AvatarUtils.getClientEntity();
        if (entity == null || entity.isRemoved()) ci.cancel();
    }
}