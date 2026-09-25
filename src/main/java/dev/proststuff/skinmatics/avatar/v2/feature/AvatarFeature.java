package dev.proststuff.skinmatics.avatar.v2.feature;

import com.mojang.serialization.Codec;
import org.jspecify.annotations.NonNull;

public interface AvatarFeature<S> {
    @NonNull Codec<S> getSettingsCodec();
}
