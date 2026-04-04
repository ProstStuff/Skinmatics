package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.utility.SkinmaticsJsonUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SkinmaticsTextureManager {
    private final Map<String, SkinmaticsTextureData> loaded = new HashMap<>();

    public SkinmaticsTextureManager() {}

    public TextureManager getTextureManager() {
        return MinecraftClient.getInstance().getTextureManager();
    }

    public SkinmaticsTextureData createTextureData(String texturePath, Identifier textureId, int width, int height, SkinmaticsTextureData.TextureSource textureSource) {
        return loaded.computeIfAbsent(textureId.toString(), (_p) -> new SkinmaticsTextureData(texturePath, textureId, width, height, textureSource));
    }

    public SkinmaticsTextureData getTextureData(Identifier textureId) {
        return loaded.computeIfAbsent(textureId.toString(), SkinmaticsTextureData::empty);
    }

    public SkinmaticsTextureData getTextureData(String texturePath) {
        if (texturePath.contains(":")) {
            Identifier textureId = Identifier.tryParse(texturePath);
            if (textureId == null) {
                return SkinmaticsTextureData.empty(texturePath);
            }
            return getTextureData(textureId);
        }
        return getTextureData(Skinmatics.of(texturePath));
    }

    public SkinmaticsTextureData resolve(String texturePath) {
        Identifier parsed = Identifier.tryParse(texturePath);

        if (parsed != null && texturePath.contains(":")) {
            AbstractTexture texture = getTextureManager().getTexture(parsed);
            int width = 16;
            int height = 16;

            if (texture != null) {
                width = texture.getGlTexture().getWidth(0);
                height = texture.getGlTexture().getHeight(0);
            }

            return createTextureData(texturePath, parsed, width, height, SkinmaticsTextureData.TextureSource.PARSED);
        }

        String clean = texturePath.replace("\\", "/");
        if (clean.endsWith(".png")) clean = clean.substring(0, clean.length() - 4);

        SkinmaticsTextureData data = getTextureData(clean);

        if (data.textureId == MissingSprite.getMissingSpriteId()) {
            SkinmaticsClient.LOGGER.warn("Missing texture data {} ({})", texturePath, clean);
        }

        return data;
    }

    public void reload() {
        SkinmaticsClient.LOGGER.info("Reloading Skinmatics textures...");

        loaded.forEach((texturePath, textureData) -> {
            if (textureData.textureId != MissingSprite.getMissingSpriteId()) {
                getTextureManager().destroyTexture(textureData.textureId);
            }
        });
        loaded.clear();
        load();
    }

    public void load() {
        Path dir = SkinmaticsJsonUtils.getModPath().resolve("textures");

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            SkinmaticsClient.LOGGER.error("Failed to create textures folder", e);
            return;
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".png")).forEach(path -> loadFrom(dir, path));
        } catch (IOException e) {
            SkinmaticsClient.LOGGER.error("Failed to scan textures folder", e);
        }
    }

    public void loadFrom(Path root, Path file) {
        TextureManager textureManager = getTextureManager();

        try {
            Path relative = root.relativize(file);
            String texturePath = relative.toString().replace("\\", "/").replace(".png", "");
            Identifier textureId = Skinmatics.of(texturePath);
            textureManager.destroyTexture(textureId);
            NativeImage image;

            try (InputStream stream = Files.newInputStream(file)) {
                image = NativeImage.read(stream);
            }

            textureManager.registerTexture(textureId, new NativeImageBackedTexture(() -> "skinmatics_" + texturePath, image));
            SkinmaticsTextureData textureData = new SkinmaticsTextureData(texturePath, textureId, image.getWidth(), image.getHeight(), SkinmaticsTextureData.TextureSource.CONFIG);
            loaded.put(textureId.toString(), textureData);

            SkinmaticsClient.LOGGER.info("Loaded texture path {} as {}", texturePath, textureId);
        } catch (Exception e) {
            SkinmaticsClient.LOGGER.warn("Failed to load texture from {}", file, e);
        }
    }

    public record SkinmaticsTextureData(String texturePath, Identifier textureId, int width, int height, TextureSource textureSource) {
        public static SkinmaticsTextureData empty(String texturePath) {
            return new SkinmaticsTextureData(texturePath, MissingSprite.getMissingSpriteId(), 16, 16, TextureSource.PARSED);
        }

        public enum TextureSource {
            CONFIG,
            PARSED
        }
    }
}
