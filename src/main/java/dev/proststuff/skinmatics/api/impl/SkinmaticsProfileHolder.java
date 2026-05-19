package dev.proststuff.skinmatics.api.impl;

import dev.proststuff.skinmatics.client.skinmatics.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SkinmaticsProfileHolder {
    void skinmatics$setProfile(Profile skinmaticsData);
    Profile skinmatics$getProfile();
}
