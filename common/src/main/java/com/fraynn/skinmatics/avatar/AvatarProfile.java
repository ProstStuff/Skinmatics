package com.fraynn.skinmatics.avatar;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureType;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypeRegistry;
import com.fraynn.utilitary.config.ConfigConstants;
import com.fraynn.utilitary.config.ConfigType;
import com.fraynn.utilitary.config.serialization.Comment;
import com.fraynn.utilitary.config.serialization.ConfigParser;
import com.fraynn.utilitary.config.serialization.ConfigSerializationStatus;
import com.fraynn.utilitary.config.serialization.format.JsonConfigFormat;
import com.fraynn.utilitary.config.serialization.metadata.SimpleMetadataType;
import com.fraynn.utilitary.util.SimpleIdentifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.*;

public record AvatarProfile(String name, int maxTicks, Map<Identifier, AvatarFeatureData<?>> features, List<Identifier> hiddenFeatures) implements AvatarIdentity {
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
                    AvatarFeatureType<?> feature = AvatarFeatureTypeRegistry.get(id);

                    if (feature == null) {
                        Skinmatics.LOGGER.warn("No AvatarFeature with id '{}' is currently present or registered", id);
                        return;
                    }

                    var res = decodeFeature(feature, ops, pair.getSecond());

                    res.error().ifPresent(err -> Skinmatics.LOGGER.warn("Unable to parse: {}", err));
                    res.result().ifPresent(data -> result.put(id, data));
                });

                return DataResult.success(Pair.of(result, ops.empty()));
            });
        }

        private static <S, T> DataResult<T> encodeFeature(AvatarFeatureData<S> featureData, DynamicOps<T> ops) {
            return featureData.feature().codec().encodeStart(ops, featureData.settings());
        }

        private static <S, T> DataResult<AvatarFeatureData<S>> decodeFeature(AvatarFeatureType<S> featureType, DynamicOps<T> ops, T input) {
            return featureType.codec().parse(ops, input).map(settings -> new AvatarFeatureData<>(featureType, settings));
        }
    };

    public static final Codec<AvatarProfile> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.STRING.fieldOf("name").forGetter(AvatarProfile::name),
                    Codec.INT.fieldOf("maxTicks").forGetter(AvatarProfile::maxTicks),
                    SETTINGS_CODEC.fieldOf("features").forGetter(AvatarProfile::features),
                    Identifier.CODEC.listOf().fieldOf("hiddenFeatures").forGetter(AvatarProfile::hiddenFeatures)
            ).apply(inst, AvatarProfile::new)
    );

    public static ConfigType<AvatarProfile, SimpleMetadataType.Metadata> TYPE = new ConfigType<>(
            Skinmatics.of("avatar"),
            2,
            ConfigType.fileCodec(SimpleMetadataType.CODEC, CODEC),
            (id) -> new AvatarProfile(AvatarUtils.fromPathIdentifier(id)),
            SimpleMetadataType.INSTANCE,
            JsonConfigFormat.INSTANCE,
            Comment.EMPTY
    );

    public static Set<String> PROFILE_DATA_KEYS = Set.of(
            "name",
            "maxTicks",
            "features",
            "hiddenFeatures"
    );

    static {
        JsonConfigFormat.FIXERS.put(TYPE, (fileId, rootElement) -> {
            JsonElement dataElement = rootElement.getAsJsonObject().remove(ConfigConstants.DATA_KEY);

            if (dataElement.isJsonObject()) {
                JsonObject dataObject = dataElement.getAsJsonObject();
                JsonObject fixedData = new JsonObject();
                JsonObject features = Optional.ofNullable(dataObject.get("features")).orElse(new JsonObject()).getAsJsonObject();

                fixedData.add("name", dataObject.remove("name"));
                fixedData.add("maxTicks", dataObject.remove("maxTicks"));
                fixedData.add("features", features);
                fixedData.add("hiddenFeatures", Optional.ofNullable(dataObject.get("hiddenFeatures")).orElse(new JsonArray()).getAsJsonArray());

                Iterator<Map.Entry<String, JsonElement>> iterator = dataObject.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String,JsonElement> entry = iterator.next();
                    String key = entry.getKey();
                    Skinmatics.LOGGER.info("adding {}", key);
                    if (PROFILE_DATA_KEYS.contains(key)) continue;

                    JsonElement element = entry.getValue();
                    Identifier id = Identifier.read(key).getOrThrow();
                    AvatarFeatureType<?> avatarFeature = AvatarFeatureTypeRegistry.get(id);

                    if (avatarFeature != null) {
                        element = avatarFeature.dataFixer().fix(element, dataObject);
                    }

                    features.add(id.toString(), element);
                    iterator.remove();
                }

                rootElement.getAsJsonObject().add(ConfigConstants.DATA_KEY, fixedData);
                return rootElement;
            }

            Skinmatics.LOGGER.warn("Unable to migrate {}", fileId);
            return rootElement;
        });
    }

    public AvatarProfile(String name) {
        this(name, 0, new HashMap<>(), new ArrayList<>());
    }

    public AvatarProfile toMutable() {
        return new AvatarProfile(name, maxTicks, new HashMap<>(features), new ArrayList<>(hiddenFeatures));
    }

    public AvatarProfile toImmutable() {
        return new AvatarProfile(name, maxTicks, Map.copyOf(features), List.copyOf(hiddenFeatures));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <S> AvatarFeatureData<S> getFeature(AvatarFeatureType<S> avatarFeature) {
        Identifier identifier = AvatarFeatureTypeRegistry.getKey(avatarFeature);
        if (identifier == null) throw new IllegalStateException("Unregistered AvatarFeatureType");
        return (AvatarFeatureData<S>) features.get(identifier);
    }

    @Override
    public boolean isHidden(AvatarFeatureType<?> avatarFeature) {
        return hiddenFeatures.contains(avatarFeature.id());
    }

    public SimpleIdentifier toPath() {
        String path = AvatarProfileManager.INSTANCE.getKey(this);
        if (path == null) throw new IllegalStateException(name + " AvatarData is not registered");
        return AvatarUtils.toPathIdentifier(path);
    }

    public ConfigSerializationStatus save() {
        return ConfigParser.write(TYPE, toPath(), this);
    }
}