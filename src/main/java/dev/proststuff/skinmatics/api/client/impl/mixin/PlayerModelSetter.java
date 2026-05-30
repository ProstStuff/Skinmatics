package dev.proststuff.skinmatics.api.client.impl.mixin;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public interface PlayerModelSetter {
    void skinmatics$setWide(PlayerModel model);
    void skinmatics$setSlim(PlayerModel model);
    void skinmatics$setCurrent(PlayerModel model);


    void skinmatics$setWideArmor(ArmorModelSet<PlayerModel> modelSet);
    void skinmatics$setSlimArmor(ArmorModelSet<PlayerModel> modelSet);
    void skinmatics$setCurrentArmor(ArmorModelSet<PlayerModel> modelSet);
}
