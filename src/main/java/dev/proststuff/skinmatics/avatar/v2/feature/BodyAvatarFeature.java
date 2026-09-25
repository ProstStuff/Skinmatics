package dev.proststuff.skinmatics.avatar.v2.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.v1.texture.Animatable;
import org.jspecify.annotations.NonNull;

public class BodyAvatarFeature implements AvatarFeature<BodyAvatarFeature.Settings> {
    public static final Codec<BodyAvatarFeature.Settings> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
              Codec.BOOL.fieldOf("slim").forGetter(Settings::slim),
              Codec.BOOL.fieldOf("customBaseTextures").forGetter(Settings::customBaseTexture),
              Codec.BOOL.fieldOf("emissive").forGetter(Settings::emissive),
              Animatable.CODEC.fieldOf("baseTextures").forGetter(Settings::baseTextures),
              Animatable.CODEC.fieldOf("emissiveTextures").forGetter(Settings::emissiveTextures)
      ).apply(inst, Settings::new)
    );

    @Override
    public @NonNull Codec<Settings> getSettingsCodec() {
        return CODEC;
    }

    public record Settings(boolean slim, boolean customBaseTexture, boolean emissive, Animatable baseTextures, Animatable emissiveTextures) {}
}
