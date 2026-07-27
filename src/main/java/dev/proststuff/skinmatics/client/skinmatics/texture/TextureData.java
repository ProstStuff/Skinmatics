package dev.proststuff.skinmatics.client.skinmatics.texture;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record TextureData(Identifier id, String path) {
    public static final Codec<TextureData> CODEC = Codec.STRING.xmap(SkinmaticsTextureManager::getOrLoad, TextureData::path);
    public static final TextureData EMPTY = new TextureData(MissingTextureAtlasSprite.getLocation(), MissingTextureAtlasSprite.getLocation().toString());

    public AbstractTexture getTexture() {
        return Minecraft.getInstance().getTextureManager().getTexture(id);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.id.equals(MissingTextureAtlasSprite.getLocation());
    }

    public static TextureData reference(String path) {
        Identifier id = null;

        if (path.contains(":")) id = Identifier.tryParse(path);
        if (id == null) id = SkinmaticsTextureManager.toIdentifier(path);

        return new TextureData(id, path);
    }

    @Override
    public @NonNull String toString() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof TextureData(Identifier id1, String path1)) return id.equals(id1) || Objects.equals(path, path1);
        return false;
    }
}