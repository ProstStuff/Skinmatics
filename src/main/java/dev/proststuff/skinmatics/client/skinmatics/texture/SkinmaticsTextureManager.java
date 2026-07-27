package dev.proststuff.skinmatics.client.skinmatics.texture;

import com.mojang.blaze3d.platform.NativeImage;
import dev.proststuff.skinmatics.SkinmaticsMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class SkinmaticsTextureManager {
    public static Path PATH = FabricLoader.getInstance().getConfigDir().resolve(SkinmaticsMod.ID).resolve("textures");
    public static Map<Identifier, TextureData> EXTERNAL_TEXTURES = new HashMap<>();
    public static Map<Identifier, TextureData> TEXTURE_DATA = new HashMap<>();

    public static TextureManager textureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    public static TextureData getOrLoad(String value) {
        Identifier id;

        if (value.contains(":")) {
            id = Identifier.tryParse(value);
            if (id != null) return get(id);
        }

        id = toIdentifier(value);
        if (EXTERNAL_TEXTURES.containsKey(id)) return get(id);
        return load(PATH.resolve(value));
    }

    public static TextureData get(Identifier id) {
        return TEXTURE_DATA.computeIfAbsent(id, (_) -> EXTERNAL_TEXTURES.getOrDefault(id, new TextureData(id, id.toString())));
    }

    public static void unload(Identifier id) {
        textureManager().release(id);
        EXTERNAL_TEXTURES.remove(id);
        TEXTURE_DATA.values().removeIf(data -> data.id().equals(id));
    }

    public static void unloadAll() {
        for (Identifier identifier : EXTERNAL_TEXTURES.keySet()) {
            unload(identifier);
        }

        TEXTURE_DATA.clear();
    }

    public static TextureData load(Identifier id, String path, NativeImage image) {
        DynamicTexture texture = new DynamicTexture(id::toString, image);
        TextureData data = new TextureData(id, path);
        textureManager().register(id, texture);
        EXTERNAL_TEXTURES.put(id, data);
        TEXTURE_DATA.put(id, data);

        SkinmaticsMod.LOGGER.info("[SKINMATICS TEXTURES] Loaded {} as {}", path, id);
        return data;
    }

    public static TextureData load(Path file) {
        try {
            Path relative = PATH.relativize(file);
            String path = relative.toString().replace('\\', '/');
            Identifier id = toIdentifier(path);

            if (EXTERNAL_TEXTURES.containsKey(id)) {
                throw new IllegalStateException("Texture %s already exists and has not unloaded yet".formatted(id));
            }

            NativeImage image;

            try (InputStream stream = Files.newInputStream(file)) {
                image = NativeImage.read(stream);
            }

            return load(id, path, image);
        } catch (Exception e) {
            SkinmaticsMod.LOGGER.error("[SKINMATICS TEXTURE HANDLER] Unable to load an image file:", e);
        }

        return null;
    }

    public static void reload() {
        unloadAll();

        try {
            Files.createDirectories(PATH);

            try (Stream<Path> paths = Files.walk(PATH)) {
                paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".png")).forEach(SkinmaticsTextureManager::load);
            }

        } catch (IOException e) {
            SkinmaticsMod.LOGGER.error("[SKINMATICS TEXTURE HANDLER] Failed loading states", e);
        }
    }

    public static Identifier toIdentifier(String path) {
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }

        return SkinmaticsMod.of(path.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase());
    }
}