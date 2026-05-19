package dev.proststuff.skinmatics.api.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public record Animatable(Map<String, List<Integer>> textures) {
    public Animatable() {
        this(new HashMap<>());
    }

    public boolean isEmpty() {
        return textures == null || textures.isEmpty();
    }

    public void clear() {
        textures.clear();
    }
}
