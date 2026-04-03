package dev.proststuff.skinmatics.client.skinmatics;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record Animatable(Map<String, List<Integer>> textures) {
    public Animatable() {this(new HashMap<>());}

    public boolean isEmpty() {
        return textures == null || textures.isEmpty();
    }

    public static class AnimatableSerializer implements JsonSerializer<Animatable>, JsonDeserializer<Animatable> {
        @Override
        public Animatable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                Map<String, List<Integer>> textures = new HashMap<>();
                Map<String, JsonElement> jsonElementMap = jsonObject.asMap();

                jsonElementMap.forEach((key, value) -> {
                    List<Integer> deserializedTicks = new ArrayList<>();
                    value.getAsJsonArray().forEach(element -> deserializedTicks.add(element.getAsInt()));
                    textures.put(key, deserializedTicks);
                });

                return new Animatable(textures);
            }

            return new Animatable();
        }

        @Override
        public JsonElement serialize(Animatable src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject animatable = new JsonObject();
            src.textures.forEach((key, value) -> animatable.add(key, context.serialize(value)));
            return animatable;
        }
    }
}
