package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record AvatarFeatureType<S>(Identifier id, Codec<S> codec) {
}
