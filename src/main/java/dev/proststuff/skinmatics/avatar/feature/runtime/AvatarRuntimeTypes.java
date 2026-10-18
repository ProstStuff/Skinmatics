package dev.proststuff.skinmatics.avatar.feature.runtime;

import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.eye.Eyes;

public interface AvatarRuntimeTypes {
    AvatarRuntimeType<Eyes, EyesAvatarRuntime> EYES = AvatarRuntimeTypeRegistry.register(
            AvatarFeatureTypes.EYES,
            factory -> factory.factory(EyesAvatarRuntime::new).ticking()
    );

    static void init() {}
}
