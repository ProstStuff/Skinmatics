package dev.proststuff.skinmatics.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public interface ForceRenderLayer {
    boolean shouldRenderWhenInvisible();

    static boolean isTrulyInvisible(LivingEntityRenderState state) {
        return state.isInvisible || state.isInvisibleToPlayer;
    }

    static boolean isTrulyInvisible(AvatarRenderState state) {
        return !state.isSpectator && isTrulyInvisible((LivingEntityRenderState) state);
    }
}