package com.fraynn.skinmatics.avatar.feature.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.feature.Animatable;
import com.fraynn.skinmatics.avatar.feature.type.eye.Eyes;
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
    ), (featureElement, dataElement) -> {
        JsonObject object = featureElement.getAsJsonObject();

        JsonObject dataObject = dataElement.getAsJsonObject();
        JsonElement strongerEmissiveElement = dataObject.has("strongerEmissive") ? dataObject.remove("strongerEmissive") : null;
        boolean strongerEmissive = strongerEmissiveElement != null && strongerEmissiveElement.isJsonPrimitive() && strongerEmissiveElement.getAsBoolean();

        if (!object.has("strongerEmissive")) {
            object.addProperty("strongerEmissive", strongerEmissive);
        }

        if (!object.has("scale")) {
            JsonArray scale = new JsonArray(2);
            scale.add(1.0F);
            scale.add(1.0F);
            object.add("scale", scale);
        }

        return object;
    });

    AvatarFeatureType<Cape> CAPE = register("cape", Cape.CODEC, () -> new Cape(
            false, false, false,
            new Animatable(), new Animatable()
    ), (featureElement, dataElement) -> {
        JsonObject object = featureElement.getAsJsonObject();

        JsonObject dataObject = dataElement.getAsJsonObject();
        JsonElement strongerEmissiveElement = dataObject.has("strongerEmissive") ? dataObject.remove("strongerEmissive") : null;
        boolean strongerEmissive = strongerEmissiveElement != null && strongerEmissiveElement.isJsonPrimitive() && strongerEmissiveElement.getAsBoolean();

        if (!object.has("strongerEmissive")) {
            object.addProperty("strongerEmissive", strongerEmissive);
        }

        return object;
    });

    AvatarFeatureType<Wings> WINGS = register("wings", Wings.CODEC, () -> new Wings(
            false, false, false,
            new Animatable(), new Animatable()
    ), (featureElement, dataElement) -> {
        JsonObject object = featureElement.getAsJsonObject();

        JsonObject dataObject = dataElement.getAsJsonObject();
        JsonElement strongerEmissiveElement = dataObject.has("strongerEmissive") ? dataObject.remove("strongerEmissive") : null;
        boolean strongerEmissive = strongerEmissiveElement != null && strongerEmissiveElement.isJsonPrimitive() && strongerEmissiveElement.getAsBoolean();

        if (!object.has("strongerEmissive")) {
            object.addProperty("strongerEmissive", strongerEmissive);
        }

        return object;
    });

    AvatarFeatureType<Eyes> EYES = register("eyes", Eyes.CODEC, () -> new Eyes(
            false,
            List.of(), List.of(), List.of(),
            100, 5,
            Map.of(),
            Map.of()
    ), (featureElement, dataElement) -> {
        JsonObject object = featureElement.getAsJsonObject();

        if (!object.has("strongerEmissive")) {
            object.add("strongerEmissive", new JsonArray());
        }

        return object;
    });

    AvatarFeatureType<Light<?>> LIGHTS = register("lights", Light.CODEC, () -> new Light<>(Skinmatics.of("simple"), 0),
            (featureElement, dataElement) -> {
                JsonObject object = featureElement.getAsJsonObject();

                if (!(object.has("type") && object.has("settings"))) {
                    object.addProperty("type", "skinmatics:simple");
                    JsonElement element = object.get("strength");
                    object.addProperty("settings", element != null ? element.getAsInt() : 0);
                }

                return object;
            }
    );

    static void init() {}

    private static <S> AvatarFeatureType<S> register(String name, Codec<S> codec, Supplier<S> createDefault, AvatarFeatureType.AvatarDataFixer dataFixer) {
        return AvatarFeatureTypeRegistry.register(Skinmatics.of(name), codec, createDefault, dataFixer);
    }
}
