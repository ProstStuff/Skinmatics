package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.impl.mixin.*;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(AvatarRenderState.class)
public class SkinmaticsAvatarRenderState implements UUIDHolder, ProfileHolder, PlayerModelGetter, PlayerModelSetter, EntityPreviewer, UIRendered {
    @Unique protected UUID skinmatics$uuid = null;
    @Unique protected Profile skinmatics$profile = null;
    @Unique protected PlayerModel skinmatics$wideModel;
    @Unique protected PlayerModel skinmatics$slimModel;
    @Unique protected ArmorModelSet<PlayerModel> skinmatics$wideArmor;
    @Unique protected ArmorModelSet<PlayerModel> skinmatics$slimArmor;
    @Unique protected boolean skinmatics$previewing = false;
    @Unique protected boolean skinmatics$uiRendered = false;

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
        if (model.slim) {
            throw new IllegalStateException("Trying to set a slim model to wide model storage");
        }

        skinmatics$wideModel = model;
    }

    @Override
    public void skinmatics$setSlim(PlayerModel model) {
        if (!model.slim) {
            throw new IllegalStateException("Trying to set a wide model to slim model storage");
        }

        skinmatics$slimModel = model;
    }

    @Override
    public void skinmatics$setCurrent(PlayerModel model) {
        throw new IllegalStateException("AvatarRenderState can't call skinmatics$setCurrent");
    }

    @Override
    public void skinmatics$setWideArmor(ArmorModelSet<PlayerModel> modelSet) {
        skinmatics$wideArmor = modelSet;
    }

    @Override
    public void skinmatics$setSlimArmor(ArmorModelSet<PlayerModel> modelSet) {
        skinmatics$slimArmor = modelSet;
    }

    @Override
    public void skinmatics$setCurrentArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new IllegalStateException("AvatarRenderState can't call skinmatics$setCurrentArmor");
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
    public ArmorModelSet<PlayerModel> skinmatics$getWideArmor() {
        return skinmatics$wideArmor;
    }

    @Override
    public ArmorModelSet<PlayerModel> skinmatics$getSlimArmor() {
        return skinmatics$slimArmor;
    }

    @Override
    public boolean skinmatics$isPreviewing() {
        return skinmatics$previewing;
    }

    @Override
    public void skinmatics$preview() {
        skinmatics$previewing = true;
    }

    @Override
    public void skinmatics$setUUID(UUID uuid) {
        skinmatics$uuid = uuid;
    }

    @Override
    public UUID skinmatics$getUUID() {
        return skinmatics$uuid;
    }


    @Override
    public void skinmatics$uiRender() {
        skinmatics$uiRendered = true;
    }

    @Override
    public boolean skinmatics$isUiRendered() {
        return skinmatics$uiRendered;
    }
}
