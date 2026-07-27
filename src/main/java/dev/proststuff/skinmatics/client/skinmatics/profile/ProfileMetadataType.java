package dev.proststuff.skinmatics.client.skinmatics.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.utilitary.Utilitary;
import dev.proststuff.utilitary.config.v1.serialization.metadata.ConfigMetadata;
import dev.proststuff.utilitary.config.v1.serialization.metadata.ConfigMetadataType;
import org.jspecify.annotations.NonNull;

public class ProfileMetadataType implements ConfigMetadataType<ProfileMetadataType.Metadata> {
    public static final Codec<Metadata> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.INT.fieldOf("version").forGetter(Metadata::version),
                    Codec.STRING.fieldOf("skinmatics").forGetter(Metadata::skinmatics),
                    Codec.STRING.fieldOf("utilitary").forGetter(Metadata::utilitary)
            ).apply(inst, Metadata::new)
    );

    @Override
    public @NonNull Codec<Metadata> codec() {
        return CODEC;
    }

    @Override
    public @NonNull <T> Metadata create(Context<T> context) {
        return new Metadata(context.version(), SkinmaticsMod.getVersionString(), SkinmaticsMod.getVersionString(Utilitary.ID));
    }

    public record Metadata(int version, String skinmatics, String utilitary) implements ConfigMetadata {}
}