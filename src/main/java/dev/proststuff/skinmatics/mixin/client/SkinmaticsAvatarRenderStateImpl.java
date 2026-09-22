package dev.proststuff.skinmatics.mixin.client;

import dev.proststuff.skinmatics.api.v1.client.AvatarTrackedEntity;
import dev.proststuff.skinmatics.api.v1.client.SkinmaticsAvatarRenderState;
import dev.proststuff.skinmatics.client.avatar.AvatarTracker;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class SkinmaticsAvatarRenderStateImpl implements AvatarTrackedEntity, SkinmaticsAvatarRenderState {
    @Unique protected AvatarTracker skinmatics$tracker = null;
    @Unique protected boolean skinmatics$eyesClosed = false;

    @Override
    public void skinmatics$apply(AvatarTracker tracker, boolean eyesClosed) {
        this.skinmatics$tracker = tracker;
        this.skinmatics$eyesClosed = eyesClosed;
    }

    @Override
    public AvatarTracker skinmatics$getTracker() {
        return skinmatics$tracker;
    }

    @Override
    public boolean skinmatics$eyesClosed() {
        return skinmatics$eyesClosed;
    }
}
