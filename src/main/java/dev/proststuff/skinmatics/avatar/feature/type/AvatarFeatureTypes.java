package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.feature.type.eye.Eyes;
import dev.proststuff.skinmatics.avatar.feature.type.halo.Halos;

public interface AvatarFeatureTypes {
    AvatarFeatureType<Body> BODY = register("body", Body.CODEC);
    AvatarFeatureType<Cape> CAPE = register("cape", Cape.CODEC);
    AvatarFeatureType<Wings> WINGS = register("wings", Wings.CODEC);
    AvatarFeatureType<Eyes> EYES = register("eyes", Eyes.CODEC);
    AvatarFeatureType<Light> LIGHT = register("lights", Light.CODEC);

    AvatarFeatureType<Halos> HALOS = register("halos", Halos.CODEC);

    static void init() {}

    private static <S> AvatarFeatureType<S> register(String name, Codec<S> codec) {
        return AvatarFeatureTypeRegistry.register(Skinmatics.of(name), codec);
    }
}
