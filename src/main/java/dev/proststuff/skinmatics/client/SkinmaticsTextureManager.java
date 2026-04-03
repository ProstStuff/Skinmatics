package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.utility.SkinmaticsJsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class SkinmaticsTextureManager {
    private final Set<Identifier> loadedTextures = new HashSet<>();
    private final Map<String, Identifier> textureMap = new HashMap<>();

    public SkinmaticsTextureManager() {}

    public TextureManager getTextureManager() {
        return MinecraftClient.getInstance().getTextureManager();
    }

    public Identifier resolve(String path) {
        if (path == null) return null;
        Identifier parsed = Identifier.tryParse(path);
        if (parsed != null && path.contains(":")) return parsed;

        String clean = path.replace("\\", "/");
        if (clean.endsWith(".png")) clean = clean.substring(0, clean.length() - 4);
        Identifier id = textureMap.get(clean);

        if (id == null) {
            SkinmaticsClient.LOGGER.warn("Missing texture {} ({})", path, clean);
            return MissingSprite.getMissingSpriteId();
        }

        return id;
    }

    public void reload() {
        SkinmaticsClient.LOGGER.info("Reloading Skinmatics textures...");

        for (Identifier id : loadedTextures) getTextureManager().destroyTexture(id);
        loadedTextures.clear();
        textureMap.clear();
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

    public Identifier loadFrom(Path root, Path file) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

        try {
            Path relative = root.relativize(file);
            String idPath = relative.toString().replace("\\", "/").replace(".png", "");
            Identifier id = Skinmatics.of(idPath);
            textureManager.destroyTexture(id);
            NativeImage image;

            try (InputStream stream = Files.newInputStream(file)) {
                image = NativeImage.read(stream);
            }

            textureManager.registerTexture(id, new NativeImageBackedTexture(() -> "skinmatics_" + idPath, image));
            loadedTextures.add(id);
            textureMap.put(idPath, id);

            SkinmaticsClient.LOGGER.info("Loaded {} as {}", idPath, id);
            return id;
        } catch (Exception e) {
            SkinmaticsClient.LOGGER.warn("Failed to load texture from {}", file, e);
            return MissingSprite.getMissingSpriteId();
        }
    }
}