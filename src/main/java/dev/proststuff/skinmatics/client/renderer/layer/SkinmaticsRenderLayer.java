package dev.proststuff.skinmatics.client.renderer.layer;

import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jspecify.annotations.Nullable;

public abstract class SkinmaticsRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> implements ForceRenderLayer {
    public SkinmaticsRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    public @Nullable ProfileTracker getTracker(AvatarRenderState state) {
        return state.skinmatics$getTracker();
    }

    public @Nullable Profile getProfile(AvatarRenderState state) {
        ProfileTracker tracker = getTracker(state);
        return tracker != null ? tracker.getProfile() : null;
    }

    @Override
    public boolean shouldRenderWhenInvisible() {
        return false;
    }
}
