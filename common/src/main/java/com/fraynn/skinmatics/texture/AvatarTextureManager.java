package com.fraynn.skinmatics.texture;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.compat.iris.IrisCompat;
import com.fraynn.utilitary.platform.services.IPlatformHelper;
import com.fraynn.utilitary.registry.RemovableSimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AvatarTextureManager extends RemovableSimpleRegistry<Identifier, AvatarTextureData> {
    public static final Path PATH = IPlatformHelper.INSTANCE.getConfigPath().resolve(Skinmatics.ID).resolve("t" + "extures"); // Prevent accidental refactoring
    public static final AvatarTextureManager INSTANCE = new AvatarTextureManager();

    private final Map<Identifier, AvatarTextureData> references = new HashMap<>();

    private AvatarTextureManager() {}

    public static TextureManager getTextureManager() {
        return Minecraft.getInstance().getTextureManager();
    }

    @Override
    public @NonNull AvatarTextureData get(Identifier id) {
        AvatarTextureData textureData = super.get(id);
        return textureData != null ? textureData : AvatarTextureData.EMPTY;
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
        if (IPlatformHelper.INSTANCE.isModLoaded("iris")) IrisCompat.trackTexture(id, texture);
        return super.register(id, data);
    }

    public AvatarTextureData register(AvatarTextureData data) {
        return register(data.id(), data);
    }

    public AvatarTextureData unregister(Identifier id, boolean closeTexture) {
        if (closeTexture) getTextureManager().release(id);
        return unregister(id);
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
        references.clear();
        super.unregisterAll();
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