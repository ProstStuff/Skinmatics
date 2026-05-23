package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.client.impl.*;
import dev.proststuff.skinmatics.api.client.Profile;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class SkinmaticsAvatarRenderState implements ProfileHolder, PlayerModelGetter, PlayerModelSetter, EntityPreviewer {
    @Unique protected Profile skinmatics$profile = null;
    @Unique protected PlayerModel skinmatics$wideModel;
    @Unique protected PlayerModel skinmatics$slimModel;
    @Unique protected boolean skinmatics$previewing = false;

    @Override
    public void skinmatics$setProfile(Profile profile) {
        this.skinmatics$profile = profile;
    }

    @Override
    public Profile skinmatics$getProfile() {
        return skinmatics$profile;
    }

    @Override
    public void skinmatics$setWide(PlayerModel model) {
        skinmatics$wideModel = model;
    }

    @Override
    public void skinmatics$setSlim(PlayerModel model) {
        skinmatics$slimModel = model;
    }

    @Override
    public PlayerModel skinmatics$getWide() {
        return skinmatics$wideModel;
    }

    @Override
    public PlayerModel skinmatics$getSlim() {
        return skinmatics$slimModel;
    }

    @Override
    public boolean skinmatics$isPreviewing() {
        return skinmatics$previewing;
    }

    @Override
    public void skinmatics$preview() {
        skinmatics$previewing = true;
    }
}
