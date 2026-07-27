package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.v1.client.TrackProfileEntity;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class SkinmaticsAvatarRenderState implements TrackProfileEntity {
    @Unique protected ProfileTracker skinmatics$tracker = null;

    @Override
    public void skinmatics$setTracker(ProfileTracker tracker) {
        this.skinmatics$tracker = tracker;
    }

    @Override
    public ProfileTracker skinmatics$getTracker() {
        return skinmatics$tracker;
    }
}
