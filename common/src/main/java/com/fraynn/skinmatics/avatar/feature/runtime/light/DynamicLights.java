package com.fraynn.skinmatics.avatar.feature.runtime.light;

import com.fraynn.skinmatics.Skinmatics;

public interface DynamicLights {
    DynamicLightFactoryRegistry.DynamicLightFactory<Integer> SIMPLE = DynamicLightFactoryRegistry.register(Skinmatics.of("simple"), SimpleDynamicLight::new, SimpleDynamicLight.CODEC);

    static void init() {}
}
