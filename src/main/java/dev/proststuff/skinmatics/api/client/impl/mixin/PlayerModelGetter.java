package dev.proststuff.skinmatics.api.client.impl.mixin;

import dev.proststuff.skinmatics.api.client.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;

@Environment(EnvType.CLIENT)
public interface PlayerModelGetter {
    default PlayerModel skinmatics$getWide() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default PlayerModel skinmatics$getSlim() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    default ArmorModelSet<PlayerModel> skinmatics$getWideArmor() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default ArmorModelSet<PlayerModel> skinmatics$getSlimArmor() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    
    default PlayerModel skinmatics$getModel(Profile profile) {
        return profile.slim.get() ? skinmatics$getSlim() : skinmatics$getWide();
    }
    
    default ArmorModelSet<PlayerModel> skinmatics$getArmorModel(Profile profile) {
        return profile.slim.get() ? skinmatics$getSlimArmor() : skinmatics$getWideArmor();
    }
}
