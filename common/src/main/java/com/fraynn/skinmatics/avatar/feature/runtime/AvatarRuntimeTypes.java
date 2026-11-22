package com.fraynn.skinmatics.avatar.feature.runtime;

import com.fraynn.skinmatics.avatar.feature.runtime.light.LightAvatarRuntime;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.eye.Eyes;
import com.fraynn.skinmatics.avatar.feature.type.Light;

public interface AvatarRuntimeTypes {
    AvatarRuntimeType<Eyes, EyesAvatarRuntime> EYES = AvatarRuntimeTypeRegistry.register(
            AvatarFeatureTypes.EYES,
            factory -> factory.factory(EyesAvatarRuntime::new).ticking()
    );

    AvatarRuntimeType<Light<?>, LightAvatarRuntime> LIGHTS = AvatarRuntimeTypeRegistry.register(
            AvatarFeatureTypes.LIGHTS,
            factory -> factory.factory(LightAvatarRuntime::new).ticking()
    );

    static void init() {}
}
