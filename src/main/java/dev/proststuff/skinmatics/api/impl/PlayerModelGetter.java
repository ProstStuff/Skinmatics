package dev.proststuff.skinmatics.api.impl;

import dev.proststuff.skinmatics.api.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;

@Environment(EnvType.CLIENT)
public interface PlayerModelGetter {
    PlayerModel skinmatics$getWide();
    PlayerModel skinmatics$getSlim();

    default PlayerModel skinmatics$getModel(Profile profile) {
        return profile.slim.get() ? skinmatics$getWide() : skinmatics$getSlim();
    }
}
