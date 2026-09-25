package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.v1.texture.AvatarTextureData;
import dev.proststuff.skinmatics.compat.iris.IrisCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class AvatarTextureManager implements Manageable<Identifier, AvatarTextureData> {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(Skinmatics.ID).resolve("baseTextures");
    public static final AvatarTextureManager INSTANCE = new AvatarTextureManager();

    private final Map<Identifier, AvatarTextureData> textures = new HashMap<>();
    private final Map<AvatarTextureData, Identifier> inverseTextures = new HashMap<>();
    private final Map<Identifier, AvatarTextureData> references = new HashMap<>();

    private AvatarTextureManager() {}

    public static TextureManager getTextureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    @Override
    public Map<Identifier, AvatarTextureData> getAll() {
        return textures;
    }

    @Override
    public Map<AvatarTextureData, Identifier> getInverse() {
        return inverseTextures;
    }

    @Override
    public @NonNull AvatarTextureData get(Identifier id) {
        return textures.computeIfAbsent(id, AvatarTextureData.Empty::new);
    }

    public @NonNull AvatarTextureData get(String name) {
        if (!name.endsWith(".png")) {
            name = name + ".png";
        }

        return get(Skinmatics.of(name));
    }

    @Override
    public AvatarTextureData register(Identifier id, AvatarTextureData data) {
        AbstractTexture texture = data.texture();
        getTextureManager().register(id, texture);
        if (FabricLoader.getInstance().isModLoaded("iris")) IrisCompat.trackTexture(id, texture);
        return Manageable.super.register(id, data);
    }

    public AvatarTextureData register(AvatarTextureData data) {
        return register(data.id(), data);
    }

    public AvatarTextureData unregister(Identifier id, boolean closeTexture) {
        if (closeTexture) getTextureManager().release(id);
        return textures.remove(id);
    }

    public AvatarTextureData unregister(AvatarTextureData data, boolean closeTexture) {
        return unregister(data.id(), closeTexture);
    }

    @Override
    public AvatarTextureData unregister(Identifier key) {
        return unregister(key, true);
    }

    @Override
    public void unregisterAll() {
        Manageable.super.unregisterAll();
        references.clear();
    }

    public @NonNull AvatarTextureData asReference(String path) {
        if (path.contains(":")) {
            Identifier id = Identifier.tryParse(path);

            if (id != null) return new AvatarTextureData.Reference(id, path);
        }

        String fixedPath = toTexturePath(path);
        Identifier id = Skinmatics.of(fixedPath);
        return references.computeIfAbsent(id, (_) -> new AvatarTextureData.Reference(id, fixedPath));
    }

    public static String toTexturePath(String path) {
        if (!path.endsWith(".png")) {
            path += ".png";
        }

        return path.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }
}