package com.fraynn.skinmatics.avatar.feature.type;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public record AvatarFeatureType<S>(Identifier id, Codec<S> codec, Supplier<S> createDefault, AvatarDataFixer dataFixer) {
    public Component asComponent() {
        return Component.translatable(id.getNamespace() + ".avatar_feature." + id.getPath());
    }


    @FunctionalInterface
    public interface AvatarDataFixer {
        JsonElement fix(JsonElement featureElement, JsonElement dataElement);
    }
}