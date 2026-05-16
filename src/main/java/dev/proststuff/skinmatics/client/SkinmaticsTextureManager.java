package dev.proststuff.skinmatics.client;

import com.mojang.blaze3d.platform.NativeImage;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureIdentifier;
import dev.proststuff.utilitary.utility.UtilitaryJsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SkinmaticsTextureManager {
    private final Map<Identifier, TextureData> loaded = new LinkedHashMap<>();

    public TextureManager textureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    public Map<Identifier, TextureData> textures() {
        return loaded;
    }

    public TextureData get(Identifier id) {
        return loaded.getOrDefault(id, TextureData.EMPTY);
    }

    public TextureData resolve(String path) {
        Identifier parsed = Identifier.tryParse(path);

        if (parsed != null && path.contains(":")) {
            if (!parsed.getNamespace().endsWith(".png")) {
                parsed = parsed.withSuffix(".png");
            }

            return resolveResource(parsed);
        }

        TextureIdentifier textureId = new TextureIdentifier(path);
        TextureData data = get(textureId.identifier());

        if (data.isEmpty()) {
            SkinmaticsClient.LOGGER.warn("Missing texture {}", path);
        }

        return data;
    }

    private TextureData resolveResource(Identifier id) {
        return loaded.computeIfAbsent(id, (_) -> {
            AbstractTexture texture = textureManager().getTexture(id);

            if (isMissing(texture)) {
                return TextureData.EMPTY;
            }

            int width = texture.getTexture().getWidth(0);
            int height = texture.getTexture().getHeight(0);

            return new TextureData(
                    id.getPath(),
                    new TextureIdentifier(id),
                    width,
                    height,
                    TextureData.TextureSource.RESOURCE
            );
        });
    }

    public void reload() {
        SkinmaticsClient.LOGGER.info("Reloading textures");

        for (TextureData data : loaded.values()) {
            textureManager().release(data.textureIdentifier().identifier());
        }

        loaded.clear();

        load();
    }

    public void load() {
        Path root = UtilitaryJsonUtils.getConfigPath()
                .resolve(Skinmatics.ID)
                .resolve("textures");

        try {
            Files.createDirectories(root);

            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".png")).forEach(path -> loadTexture(root, path));
            }

        } catch (IOException e) {
            SkinmaticsClient.LOGGER.error("Failed loading textures", e);
        }
    }

    private void loadTexture(Path root, Path file) {
        try {
            Path relative = root.relativize(file);
            String path = relative.toString().replace("\\", "/");
            TextureIdentifier textureId = new TextureIdentifier(path);
            Identifier id = textureId.identifier();
            textureManager().release(id);
            NativeImage image;

            try (InputStream stream = Files.newInputStream(file)) {
                image = NativeImage.read(stream);
            }

            textureManager().register(id, new DynamicTexture(textureId::toString,image));

            TextureData data = new TextureData(path, textureId, image.getWidth(), image.getHeight(), TextureData.TextureSource.MOD);
            loaded.put(id, data);
            SkinmaticsClient.LOGGER.info("Loaded texture {} -> {}", path, id);
        } catch (Exception e) {
            SkinmaticsClient.LOGGER.error("Failed loading texture {}", file, e);
        }
    }

    private boolean isMissing(AbstractTexture texture) {
        return texture == textureManager().getTexture(MissingTextureAtlasSprite.getLocation());
    }
}
