package dev.proststuff.skinmatics.api.client.impl;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface ForceRenderLayer {
    boolean alwaysRender();

    static boolean isInvisible(LivingEntityRenderState state) {
        return state.isInvisible || state.isInvisibleToPlayer;
    }

    static boolean isInvisible(AvatarRenderState state) {
        return state.isInvisibleToPlayer;
    }
}
