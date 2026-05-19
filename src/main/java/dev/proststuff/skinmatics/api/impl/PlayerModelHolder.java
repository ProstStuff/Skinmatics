package dev.proststuff.skinmatics.api.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;

@Environment(EnvType.CLIENT)
public interface PlayerModelHolder {
    PlayerModel skinmatics$getWide();
    PlayerModel skinmatics$getSlim();
}
