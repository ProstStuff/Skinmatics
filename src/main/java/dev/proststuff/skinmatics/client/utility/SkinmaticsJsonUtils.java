package dev.proststuff.skinmatics.client.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.skinmatics.Animatable;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SkinmaticsJsonUtils {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .setStrictness(Strictness.LENIENT)
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(Animatable.class, new Animatable.AnimatableSerializer())
            .create();

    public static Path getModPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(Skinmatics.ID);
    }

    public static void write(String fileName, Path store, Object data) throws IOException {
        Path path = store.resolve(fileName + ".json");
        if (!Files.exists(store)) Files.createDirectories(store);

        try (Writer writer = new FileWriter(path.toFile())) {
            gson.toJson(data, writer);
            Skinmatics.LOGGER.info("Successfully written {}.json", fileName);
        }
    }

    public static void write(String fileName, Object data) throws IOException {
        write(fileName, getModPath(), data);
    }

    public static <T> T read(String fileName, Path search, Class<T> clazz, Supplier<T> fallback) throws IOException {
        Path path = search.resolve(fileName + ".json");
        if (!Files.exists(path)) return fallback.get();

        try (Reader reader = new FileReader(path.toFile())) {
            T obj = gson.fromJson(reader, clazz);
            Skinmatics.LOGGER.info("Successfully read {}.json", fileName);
            return obj != null ? obj : fallback.get();
        } catch (Exception e) {
            Skinmatics.LOGGER.warn("Failed to read {}.json", fileName, e);
            return fallback.get();
        }
    }

    public static <T> T read(String fileName, Class<T> clazz, Supplier<T> fallback) throws IOException {
        return read(fileName, getModPath(), clazz, fallback);
    }
}
