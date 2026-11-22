package com.fraynn.skinmatics.avatar.pack;

import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.fraynn.skinmatics.texture.AvatarTextureData;
import com.fraynn.utilitary.util.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jspecify.annotations.NonNull;

import java.util.*;

public record AvatarPack(Info info, Map<String, Either<AvatarProfile, String>> avatars, Set<AvatarTextureData.Direct> textures, boolean locked) {
    public AvatarPack(Info info, boolean locked) {
        this(info, new HashMap<>(), new HashSet<>(), locked);
    }

    public void registerAvatar(@NonNull String name, @NonNull Either<AvatarProfile, String> profile) {
        avatars.put(name, profile);
    }

    public void registerTexture(AvatarTextureData.Direct direct) {
        textures.add(direct);
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