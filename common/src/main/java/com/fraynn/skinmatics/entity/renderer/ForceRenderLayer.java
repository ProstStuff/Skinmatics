package com.fraynn.skinmatics.entity.renderer;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface ForceRenderLayer {
    boolean shouldRenderWhenInvisible();

    static boolean isTrulyInvisible(LivingEntityRenderState state) {
        return state.isInvisible || state.isInvisibleToPlayer;
    }

    static boolean isTrulyInvisible(AvatarRenderState state) {
        return !state.isSpectator && isTrulyInvisible((LivingEntityRenderState) state);
    }
}