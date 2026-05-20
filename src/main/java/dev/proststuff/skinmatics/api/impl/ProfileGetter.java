package dev.proststuff.skinmatics.api.impl;

import dev.proststuff.skinmatics.api.skinmatics.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ProfileGetter {
    void skinmatics$setProfile(Profile skinmaticsData);
    Profile skinmatics$getProfile();
}
