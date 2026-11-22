package com.fraynn.skinmatics.entity.renderer.layer;

import com.fraynn.skinmatics.entity.renderer.ForceRenderLayer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public abstract class SkinmaticsRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> implements ForceRenderLayer {
    public SkinmaticsRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public boolean shouldRenderWhenInvisible() {
        return false;
    }
}
