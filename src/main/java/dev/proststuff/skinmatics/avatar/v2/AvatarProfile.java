package dev.proststuff.skinmatics.avatar.v2;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.v2.feature.AvatarFeature;
import dev.proststuff.skinmatics.avatar.v2.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.v2.feature.AvatarFeatureRegistry;
import dev.proststuff.utilitary.config.v1.ConfigCodec;
import dev.proststuff.utilitary.config.v1.ConfigType;
import dev.proststuff.utilitary.config.v1.serialization.Comment;
import dev.proststuff.utilitary.config.v1.serialization.format.JsonConfigFormat;
import dev.proststuff.utilitary.config.v1.serialization.metadata.SimpleMetadataType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record AvatarProfile(String name, Map<Identifier, AvatarFeatureData<?>> avatarFeatureSettings) {
    public static final Codec<Map<Identifier, AvatarFeatureData<?>>> SETTINGS_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<T> encode(Map<Identifier, AvatarFeatureData<?>> input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();

            for (Map.Entry<Identifier, AvatarFeatureData<?>> entry : input.entrySet()) {
                Identifier id = entry.getKey();
                AvatarFeatureData<?> data = entry.getValue();
                DataResult<T> encoded = encodeFeature(data, ops);

                encoded.error().ifPresent(error -> builder.withErrorsFrom(DataResult.error(error::message)));
                encoded.result().ifPresent(value -> builder.add(id.toString(), value));
            }

            return builder.build(prefix);
        }

        @Override
        public <T> DataResult<Pair<Map<Identifier, AvatarFeatureData<?>>, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(map -> {
                Map<Identifier, AvatarFeatureData<?>> result = new HashMap<>();

                map.entries().forEach(pair -> {
                    DataResult<Identifier> idResult = Identifier.read(ops.getStringValue(pair.getFirst()).result().orElse(""));
                    if (idResult.result().isEmpty()) return;

                    Identifier id = idResult.result().get();
                    AvatarFeature<?> feature = AvatarFeatureRegistry.get(id);

                    if (feature == null) {
                        Skinmatics.LOGGER.warn("No AvatarFeature with id '{}' is present or registered", id);
                        return;
                    }

                    decodeFeature(feature, ops, pair.getSecond()).result().ifPresent(data -> result.put(id, data));
                });

                return DataResult.success(Pair.of(result, ops.empty()));
            });
        }

        private static <S, T> DataResult<T> encodeFeature(AvatarFeatureData<S> data, DynamicOps<T> ops) {
            return data.avatarFeature().getSettingsCodec().encodeStart(ops, data.settings());
        }

        private static <S, T> DataResult<AvatarFeatureData<S>> decodeFeature(AvatarFeature<S> feature, DynamicOps<T> ops, T input) {
            return feature.getSettingsCodec().parse(ops, input).map(settings -> new AvatarFeatureData<>(feature, settings));
        }
    };

    public static final Codec<AvatarProfile> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.STRING.fieldOf("name").forGetter(AvatarProfile::name),
                    SETTINGS_CODEC.fieldOf("features").forGetter(AvatarProfile::avatarFeatureSettings)
            ).apply(inst, AvatarProfile::new)
    );

    public static ConfigType<AvatarProfile, SimpleMetadataType.Metadata, JsonConfigFormat.FormatSettings> CONFIG_TYPE = new ConfigType<>(
            Skinmatics.of("avatar"),
            2,
            ConfigCodec.of(CODEC),
            (_) -> null,
            List.of(),
            SimpleMetadataType.INSTANCE,
            JsonConfigFormat.INSTANCE,
            Comment.EMPTY
    );

    private static JsonElement migrate(ConfigType.Migration.Context context) {
        return context.data();
    }
}