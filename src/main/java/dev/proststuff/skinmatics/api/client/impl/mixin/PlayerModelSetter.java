package dev.proststuff.skinmatics.api.client.impl.mixin;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public interface PlayerModelSetter {
    default void skinmatics$setWide(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default void skinmatics$setSlim(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default void skinmatics$setCurrent(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default void skinmatics$setWideArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default void skinmatics$setSlimArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default void skinmatics$setCurrentArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
}
