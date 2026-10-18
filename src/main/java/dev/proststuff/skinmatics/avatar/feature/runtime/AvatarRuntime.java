package dev.proststuff.skinmatics.avatar.feature.runtime;

import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;

public abstract class AvatarRuntime<S> {
    protected final AvatarTracker tracker;
    protected final AvatarFeatureData<S> featureData;

    public AvatarRuntime(AvatarTracker tracker, AvatarFeatureData<S> featureData) {
        this.tracker = tracker;
        this.featureData = featureData;
    }

    public AvatarTracker getTracker() {
        return tracker;
    }

    public AvatarFeatureData<S> getData() {
        return featureData;
    }

    public void tick() {}
}
