package dev.proststuff.skinmatics.api.impl;

import dev.proststuff.skinmatics.api.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ProfileHolder {
    void skinmatics$setProfile(Profile skinmaticsData);
    Profile skinmatics$getProfile();
}
