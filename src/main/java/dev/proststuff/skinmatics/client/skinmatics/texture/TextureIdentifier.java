package dev.proststuff.skinmatics.client.skinmatics.texture;

import dev.proststuff.skinmatics.Skinmatics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record TextureIdentifier(Identifier identifier) {
    public static final TextureIdentifier EMPTY = new TextureIdentifier(MissingTextureAtlasSprite.getLocation());

    public TextureIdentifier(String path) {
        this(Skinmatics.of(normalize(path)));
    }

    private static String normalize(String path) {
        path = path.replace("\\", "/");

        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }

        return path.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    public boolean isEmpty() {
        return identifier.equals(MissingTextureAtlasSprite.getLocation());
    }

    @Override
    public @NonNull String toString() {
        return identifier.toString();
    }
}
