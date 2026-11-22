package com.fraynn.skinmatics.mixin.client.feature;

import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.Body;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.PlayerModelType;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class BodyModelSwapperAvatarFeature {
    @ModifyExpressionValue(method = "getAvatarRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;model()Lnet/minecraft/world/entity/player/PlayerModelType;"))
    public <T extends Avatar & ClientAvatarEntity> PlayerModelType skinmatics$interceptPlayerModel(PlayerModelType original, final Map<PlayerModelType, AvatarRenderer<@NonNull T>> renderers, final T entity) {
        AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);

        if (tracker == null) return original;
        AvatarFeatureData<Body> featureData = tracker.getAvatar().getFeature(AvatarFeatureTypes.BODY);
        if (featureData == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return original;
        return featureData.settings().slim() ? PlayerModelType.SLIM : PlayerModelType.WIDE;

    }

    @ModifyExpressionValue(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;model()Lnet/minecraft/world/entity/player/PlayerModelType;"))
    public PlayerModelType skinmatics$interceptPlayerModelFromRenderState(PlayerModelType original, EntityRenderState entityRenderState) {
        if (entityRenderState instanceof AvatarRenderState state && ((AvatarTrackedEntity) state).skinmatics$getTracker() instanceof AvatarTracker tracker) {
            AvatarFeatureData<Body> featureData = tracker.getAvatar().getFeature(AvatarFeatureTypes.BODY);
            if (featureData == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return original;
            return featureData.settings().slim() ? PlayerModelType.SLIM : PlayerModelType.WIDE;
        }

        return original;
    }
}