package dev.proststuff.skinmatics.api.client.impl.mixin;

import dev.proststuff.skinmatics.api.client.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;

@Environment(EnvType.CLIENT)
public interface PlayerModelGetter {
    PlayerModel skinmatics$getWide();
    PlayerModel skinmatics$getSlim();

    ArmorModelSet<PlayerModel> skinmatics$getWideArmor();
    ArmorModelSet<PlayerModel> skinmatics$getSlimArmor();
    
    default PlayerModel skinmatics$getModel(Profile profile) {
        return profile.slim.get() ? skinmatics$getSlim() : skinmatics$getWide();
    }
    
    default ArmorModelSet<PlayerModel> skinmatics$getArmorModel(Profile profile) {
        return profile.slim.get() ? skinmatics$getSlimArmor() : skinmatics$getWideArmor();
    }
}
