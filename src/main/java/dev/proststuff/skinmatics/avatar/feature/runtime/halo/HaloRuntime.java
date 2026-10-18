package dev.proststuff.skinmatics.avatar.feature.runtime.halo;

import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntime;
import dev.proststuff.skinmatics.avatar.feature.type.halo.Halos;

public class HaloRuntime extends AvatarRuntime<Halos> {
    public HaloRuntime(AvatarTracker tracker, AvatarFeatureData<Halos> featureData) {
        super(tracker, featureData);
    }
}
