package dev.proststuff.skinmatics.api.config.field;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.proststuff.skinmatics.api.config.Animatable;
import dev.proststuff.utilitary.api.config.codec.ConfigCodec;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimatableConfigField extends ConfigField<Animatable> {
    public static final ConfigCodec<Animatable> CODEC = new ConfigCodec<>(
            (animatable, context) -> {
                JsonObject object = new JsonObject();
                animatable.textures().forEach((key, value) -> object.add(key, context.serialize(value)));
                return object;
            },
            (jsonElement, _) -> {
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
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
    );

    public AnimatableConfigField(String name) {
        super(name, new Animatable(), CODEC);
    }
}
