package dev.proststuff.skinmatics.mixin.client.skin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.proststuff.skinmatics.client.skinmatics.TrackedProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
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
public class SkinmaticsModelSwapper {
    @ModifyExpressionValue(method = "getAvatarRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;model()Lnet/minecraft/world/entity/player/PlayerModelType;"))
    public <T extends Avatar & ClientAvatarEntity> PlayerModelType skinmatics$interceptPlayerModel(PlayerModelType original, final Map<PlayerModelType, AvatarRenderer<@NonNull T>> renderers, final T entity) {
        ProfileTracker entityTracker = TrackedProfileManager.get(entity);

        if (entityTracker != null) return entityTracker.getProfile().skin().slim() ? PlayerModelType.SLIM : PlayerModelType.WIDE;
        return original;
    }

    @ModifyExpressionValue(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;model()Lnet/minecraft/world/entity/player/PlayerModelType;"))
    public PlayerModelType skinmatics$interceptPlayerModelFromRenderState(PlayerModelType original, EntityRenderState entityRenderState) {
        if (entityRenderState instanceof AvatarRenderState state && state.skinmatics$getProfile() != null) {
            return state.skinmatics$getProfile().skin().slim() ? PlayerModelType.SLIM : PlayerModelType.WIDE;
        }

        return original;
    }
}