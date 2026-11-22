package com.fraynn.skinmatics.mixin.client;

import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class SkinmaticsAvatarRenderState implements AvatarTrackedEntity {
    @Unique protected AvatarTracker skinmatics$tracker = null;

    @Override
    public void skinmatics$setTracker(AvatarTracker avatarTracker) {
        skinmatics$tracker = avatarTracker;
    }

    @Override
    public AvatarTracker skinmatics$getTracker() {
        return skinmatics$tracker;
    }
}
