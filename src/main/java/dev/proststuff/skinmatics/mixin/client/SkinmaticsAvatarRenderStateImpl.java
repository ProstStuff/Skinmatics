package dev.proststuff.skinmatics.mixin.client;

import dev.proststuff.skinmatics.entity.AvatarTrackedEntity;
import dev.proststuff.skinmatics.entity.SkinmaticsAvatarRenderState;
import dev.proststuff.skinmatics.avatar.v1.AvatarTracker;
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
