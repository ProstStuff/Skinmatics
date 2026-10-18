package dev.proststuff.skinmatics.avatar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypeRegistry;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.manager.AvatarProfileManager;
import dev.proststuff.utilitary.config.ConfigConstants;
import dev.proststuff.utilitary.config.ConfigType;
import dev.proststuff.utilitary.config.serialization.Comment;
import dev.proststuff.utilitary.config.serialization.ConfigParser;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationStatus;
import dev.proststuff.utilitary.config.serialization.format.JsonConfigFormat;
import dev.proststuff.utilitary.config.serialization.metadata.SimpleMetadataType;
import dev.proststuff.utilitary.util.SimpleIdentifier;
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
                    Skinmatics.LOGGER.info("decoding {}", id);
                    AvatarFeatureType<?> feature = AvatarFeatureTypeRegistry.get(id);

                    if (feature == null) {
                        Skinmatics.LOGGER.warn("No AvatarFeature with id '{}' is present or registered. Ignored decoding.", id);
                        return;
                    }

                    var res = decodeFeature(feature, ops, pair.getSecond());
                    res.error().ifPresent(
                            err -> {
                                Skinmatics.LOGGER.warn("Unable to parse: {}", err);
                            }
                    );

                    res.result().ifPresent(data -> {
                        Skinmatics.LOGGER.info("Successfully decoded {}", id);
                        result.put(id, data);
                    });
                });

                return DataResult.success(Pair.of(result, ops.empty()));
            });
        }

        private static <S, T> DataResult<T> encodeFeature(AvatarFeatureData<S> featureData, DynamicOps<T> ops) {
            return featureData.feature().codec().encodeStart(ops, featureData.settings());
        }

        private static <S, T> DataResult<AvatarFeatureData<S>> decodeFeature(AvatarFeatureType<S> featureData, DynamicOps<T> ops, T input) {
            return featureData.codec().parse(ops, input).map(settings -> new AvatarFeatureData<>(featureData, settings));
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

    static {
        JsonConfigFormat.FIXERS.put(TYPE, (fileId, rootElement) -> {
            JsonElement metadata = rootElement.getAsJsonObject().get(ConfigConstants.METADATA_KEY);
            if (metadata.isJsonPrimitive() && metadata.getAsInt() == 2) return rootElement;

            Set<String> nonFeatureKeys = Set.of(
                    "name",
                    "maxTicks",
                    "features",
                    "hiddenFeatures"
            );

            Set<AvatarFeatureType<?>> emissiveInstance = Set.of(
                    AvatarFeatureTypes.BODY,
                    AvatarFeatureTypes.CAPE,
                    AvatarFeatureTypes.WINGS,
                    AvatarFeatureTypes.EYES
            );

            JsonElement dataElement = rootElement.getAsJsonObject().remove(ConfigConstants.DATA_KEY);

            if (dataElement.isJsonObject()) {
                JsonObject dataObject = dataElement.getAsJsonObject();
                JsonObject fixedData = new JsonObject();
                JsonObject features = new JsonObject();

                fixedData.add("name", dataObject.remove("name"));
                fixedData.add("maxTicks", dataObject.remove("maxTicks"));
                fixedData.add("features", features);
                fixedData.add("hiddenFeatures", new JsonArray());

                JsonElement strongerEmissiveElement = dataObject.has("strongerEmissive") ? dataObject.remove("strongerEmissive") : null;
                boolean strongerEmissive = strongerEmissiveElement != null && strongerEmissiveElement.isJsonPrimitive() && strongerEmissiveElement.getAsBoolean();

                Iterator<Map.Entry<String, JsonElement>> iterator = dataObject.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String,JsonElement> entry = iterator.next();
                    String key = entry.getKey();
                    Skinmatics.LOGGER.info("Migrating '{}'...", key);

                    if (nonFeatureKeys.contains(key)) continue;
                    JsonElement element = entry.getValue();
                    Identifier id = Skinmatics.of(key);
                    AvatarFeatureType<?> avatarFeature = AvatarFeatureTypeRegistry.get(id);

                    if (avatarFeature != null) {
                        if (emissiveInstance.contains(avatarFeature) && element.isJsonObject()) {
                            if (avatarFeature != AvatarFeatureTypes.EYES) {
                                element.getAsJsonObject().addProperty("strongerEmissive", strongerEmissive);
                            } else {
                                element.getAsJsonObject().add("strongerEmissive", new JsonArray());
                            }
                        }
                    } else {
                        Skinmatics.LOGGER.warn("Unknown field, {} does not have a registered AvatarFeatureType. Can't migrate this field!", id);
                    }

                    features.add(id.toString(), element);
                    iterator.remove();
                }

                rootElement.getAsJsonObject().add(ConfigConstants.DATA_KEY, fixedData);
                Skinmatics.LOGGER.info("{}", rootElement);
                return rootElement;
            }

            Skinmatics.LOGGER.warn("Unable to migrate {}", fileId);
            return rootElement;
        });
    }

    public AvatarProfile(String name) {
        this(name, 0, new HashMap<>(), new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <S> AvatarFeatureData<S> getFeature(AvatarFeatureType<S> avatarFeature) {
        Identifier identifier = AvatarFeatureTypeRegistry.getKey(avatarFeature);
        if (identifier == null) throw new IllegalStateException("Unregistered AvatarFeatureType");
        return (AvatarFeatureData<S>) features.get(identifier);
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