package dev.proststuff.skinmatics.avatar.feature.type;

import com.mojang.serialization.Codec;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.feature.Animatable;
import dev.proststuff.skinmatics.avatar.feature.type.eye.Eyes;
import net.minecraft.world.phys.Vec2;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface AvatarFeatureTypes {
    AvatarFeatureType<Body> BODY = register("body", Body.CODEC, () -> new Body(
            false,
            new Vec2(1.0F, 1.0F),
            false, false, false,
            new Animatable(), new Animatable()
    ));
    AvatarFeatureType<Cape> CAPE = register("cape", Cape.CODEC, () -> new Cape(
            false, false, false,
            new Animatable(), new Animatable()
    ));
    AvatarFeatureType<Wings> WINGS = register("wings", Wings.CODEC, () -> new Wings(
            false, false, false,
            new Animatable(), new Animatable()
    ));
    AvatarFeatureType<Eyes> EYES = register("eyes", Eyes.CODEC, () -> new Eyes(
            false,
            List.of(), List.of(), List.of(),
            100, 5,
            Map.of(),
            Map.of()
    ));
    AvatarFeatureType<Light> LIGHT = register("lights", Light.CODEC, () -> new Light(0));

    static void init() {}

    private static <S> AvatarFeatureType<S> register(String name, Codec<S> codec, Supplier<S> createDefault) {
        return AvatarFeatureTypeRegistry.register(Skinmatics.of(name), codec, createDefault);
    }
}
