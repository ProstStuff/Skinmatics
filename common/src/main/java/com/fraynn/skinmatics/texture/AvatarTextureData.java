package com.fraynn.skinmatics.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

public interface AvatarTextureData {
    Codec<AvatarTextureData> CODEC = Codec.STRING.xmap(AvatarTextureManager.INSTANCE::asReference, AvatarTextureData::path);
    AvatarTextureData EMPTY = new Empty(MissingTextureAtlasSprite.getLocation(), MissingTextureAtlasSprite.getLocation().getPath());

    @NonNull Identifier id();
    @NonNull String path();
    @NonNull AbstractTexture texture();

    boolean isEmpty();

    record Reference(@NonNull Identifier id, @NonNull String path) implements AvatarTextureData {
        public Reference(@NonNull Identifier id) {
            this(id, id.getPath());
        }

        public @NonNull AbstractTexture texture() {
            return AvatarTextureManager.getTextureManager().getTexture(id);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    final class Direct implements AvatarTextureData {
        private final @NonNull Identifier id;
        private final @NonNull String path;
        private final Supplier<AbstractTexture> textureFactory;
        private AbstractTexture cached = null;

        public Direct(@NonNull Identifier id, @NonNull String path, Supplier<AbstractTexture> textureFactory) {
            this.id = id;
            this.path = path;
            this.textureFactory = textureFactory;
        }

        public Direct(@NonNull Identifier id, @NonNull String path, NativeImage image) {
            this(id, path, () -> new SkinmaticsDynamicTexture(id::toString, image));
        }

        @Override
        public @NonNull AbstractTexture texture() {
            if (cached == null) cached = textureFactory.get();
            return cached;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public @NonNull Identifier id() {
            return id;
        }

        @Override
        public @NonNull String path() {
            return path;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Direct) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.path, that.path) &&
                    Objects.equals(this.textureFactory, that.textureFactory);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, path, textureFactory);
        }

        @Override
        public String toString() {
            return "Direct[" +
                    "id=" + id + ", " +
                    "path=" + path + ", " +
                    "textureFactory=" + textureFactory + ']';
        }
    }

    record Empty(@NonNull Identifier id, @NonNull String path) implements AvatarTextureData {
        public Empty(@NonNull Identifier id) {
            this(id, id.getPath());
        }

        @Override
        public @NonNull AbstractTexture texture() {
            return AvatarTextureManager.getTextureManager().getTexture(id);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }
}
