package dev.proststuff.skinmatics.api.client.impl.mixin;

import dev.proststuff.skinmatics.api.client.Profile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ProfileHolder {
    default void skinmatics$setProfile(Profile skinmaticsData) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
    default Profile skinmatics$getProfile() {
        throw new UnsupportedOperationException("Unimplemented operation");
    }
}
