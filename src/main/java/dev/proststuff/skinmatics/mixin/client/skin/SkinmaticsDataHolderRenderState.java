package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.skinmatics.impl.SkinmaticsProfileHolder;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class SkinmaticsDataHolderRenderState implements SkinmaticsProfileHolder {
    @Unique
    private Profile skinmatics$profile = null;

    @Override
    public void skinmatics$setProfile(Profile profile) {
        this.skinmatics$profile = profile;
    }

    @Override
    public Profile skinmatics$getProfile() {
        return skinmatics$profile;
    }
}
