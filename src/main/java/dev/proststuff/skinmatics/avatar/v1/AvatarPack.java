package dev.proststuff.skinmatics.avatar.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.avatar.v1.data.AvatarData;
import dev.proststuff.skinmatics.avatar.v1.texture.AvatarTextureData;
import dev.proststuff.utilitary.api.v1.util.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jspecify.annotations.NonNull;

import java.util.*;

public record AvatarPack(Info info, Map<String, Either<AvatarData, String>> avatars, Set<AvatarTextureData.Data> textures) {
    public AvatarPack(Info info) {
        this(info, new HashMap<>(), new HashSet<>());
    }

    public void registerAvatar(@NonNull String name, @NonNull Either<AvatarData, String> profile) {
        avatars.put(name, profile);
    }

    public void registerTexture(AvatarTextureData.Data data) {
        textures.add(data);
    }

    public record Info(Component name, Component description, String license, List<String> authors, String version) {
        public static final Codec<Info> CODEC = RecordCodecBuilder.create(
                inst -> inst.group(
                        ComponentSerialization.CODEC.fieldOf("name").forGetter(Info::name),
                        ComponentSerialization.CODEC.fieldOf("description").forGetter(Info::description),
                        Codec.STRING.fieldOf("license").forGetter(Info::license),
                        Codec.list(Codec.STRING).fieldOf("authors").forGetter(Info::authors),
                        Codec.STRING.fieldOf("version").forGetter(Info::version)
                ).apply(inst, Info::new)
        );
    }
}