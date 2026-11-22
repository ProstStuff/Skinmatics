package com.fraynn.skinmatics.avatar.feature.runtime.light;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.runtime.AvatarRuntime;
import com.fraynn.skinmatics.avatar.feature.type.Light;

public class LightAvatarRuntime extends AvatarRuntime<Light<?>> {
    protected final DynamicLight<?> dynamicLight;

    public LightAvatarRuntime(AvatarTracker tracker, AvatarFeatureData<Light<?>> featureData) {
        super(tracker, featureData);
        DynamicLightFactoryRegistry.DynamicLightFactory<?> factory = featureData.settings().getFactory(this).get().orElse(null);
        this.dynamicLight = factory != null ? factory.create(this) : null;
    }

    public int get() {
        return dynamicLight != null ? dynamicLight.get() : 0;
    }

    @Override
    public void tick() {
        if (dynamicLight != null) {
            dynamicLight.tick();
        }
    }
}
