package dev.proststuff.skinmatics.client.avatar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.JsonOps;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.avatar.data.AvatarData;
import dev.proststuff.skinmatics.client.manager.AvatarPackManager;
import dev.proststuff.skinmatics.client.manager.AvatarDataManager;
import dev.proststuff.skinmatics.client.manager.AvatarTextureManager;
import dev.proststuff.skinmatics.client.avatar.texture.AvatarTextureData;
import dev.proststuff.utilitary.api.v1.util.Either;
import dev.proststuff.utilitary.config.v1.ConfigManager;
import dev.proststuff.utilitary.config.v1.ConfigResult;
import dev.proststuff.utilitary.config.v1.serialization.format.JsonConfigFormat;
import dev.proststuff.utilitary.config.v1.serialization.metadata.SimpleMetadataType;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public interface AvatarPackLoader {
    static void init() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(SkinmaticsMod.of("skinmatics_pack_loader"), AvatarPackLoader::onLoad);
    }

    static CompletableFuture<Void> onLoad(PreparableReloadListener.SharedState sharedState, Executor executor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, AvatarPack> avatarPacks = new HashMap<>();

            prepareFromResourcePacks(sharedState, avatarPacks);
            prepareFromConfig(avatarPacks);
            return avatarPacks;
        }, executor)
                .thenCompose(preparationBarrier::wait)
                .thenApplyAsync((avatarPacks) -> {
                    AvatarTextureManager.INSTANCE.unregisterAll();
                    AvatarDataManager.INSTANCE.unregisterAll();

                    for (Map.Entry<String, AvatarPack> entry : avatarPacks.entrySet()) {
                        AvatarPackManager.INSTANCE.register(entry.getKey(), entry.getValue());
                    }
            return null;
        }, applyExecutor);
    }

    static void prepareFromResourcePacks(PreparableReloadListener.SharedState sharedState, Map<String, AvatarPack> avatarPacks) {
        ResourceManager resourceManager = sharedState.resourceManager();
        Map<Identifier, Resource> packs = resourceManager.listResources("skinmatics", (id) -> id.getPath().endsWith("/pack.json"));

        for (Map.Entry<Identifier, Resource> entry : packs.entrySet()) {
            Identifier id = entry.getKey();
            String[] split = id.getPath().split("/");
            String packName = split[split.length - 2];
            String packPath = "skinmatics/packs/" + packName;
            Resource pack = entry.getValue();

            try {
                AvatarPack avatarPack = new AvatarPack(
                        AvatarPack.Info.CODEC.parse(JsonOps.INSTANCE, JsonConfigFormat.GSON.fromJson(pack.openAsReader(), JsonElement.class)).getOrThrow()
                );
                prepareAvatarTextures(sharedState, packName, packPath, avatarPack);
                prepareAvatars(sharedState, packName, packPath, avatarPack);

                avatarPacks.put(packName, avatarPack);
            } catch (IOException e) {
                SkinmaticsMod.LOGGER.error("[SKINMATICS PACK LOADER | INTERNAL] Failed to load {}", id, e);
            }
        }
    }

    static void prepareAvatarTextures(PreparableReloadListener.SharedState sharedState, String packName, String packPath, AvatarPack pack) {
        ResourceManager resourceManager = sharedState.resourceManager();
        Map<Identifier, Resource> textures = resourceManager.listResources(packPath + "/textures", (i) -> i.getPath().endsWith(".png"));

        for (Map.Entry<Identifier, Resource> textureEntry : textures.entrySet()) {
            Resource resource = textureEntry.getValue();
            Identifier texture = textureEntry.getKey();
            String texturePath = texture.getPath();

            Identifier textureId = SkinmaticsMod.of(AvatarTextureManager.toTexturePath(texturePath.replace(packPath + "/textures", packName)));

            try {
                NativeImage image = NativeImage.read(resource.open());
                pack.registerTexture(new AvatarTextureData.Data(textureId, texturePath, image));
            } catch (IOException e) {
                SkinmaticsMod.LOGGER.error("[SKINMATICS TEXTURE LOADER] Failed to load image: {}", texture, e);
            }
        }
    }

    static void prepareAvatars(PreparableReloadListener.SharedState sharedState, String packName, String packPath, AvatarPack pack) {
        ResourceManager resourceManager = sharedState.resourceManager();
        Map<Identifier, Resource> profiles = resourceManager.listResources(packPath + "/avatars", (i) -> i.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> profileEntry : profiles.entrySet()) {
            Resource resource = profileEntry.getValue();
            Identifier profile = profileEntry.getKey();
            String name = profile.getPath().replace(packPath + "/avatars", packName).replace(".json", "");

            try {
                JsonElement element = JsonConfigFormat.GSON.fromJson(resource.openAsReader(), JsonObject.class);
                ConfigResult<AvatarData, SimpleMetadataType.Metadata> result = ConfigManager.read(null, AvatarData.TYPE, element.getAsJsonObject());
                pack.registerAvatar(name, Either.left(result.config()));
            } catch (Exception e) {
                SkinmaticsMod.LOGGER.warn("[SKINMATICS AVATAR LOADER] Failed to load avatar {}:", profile, e);
                pack.registerAvatar(name, Either.right(e.toString()));
            }
        }
    }


    static void prepareFromConfig(Map<String, AvatarPack> avatarPacks) {
        AvatarPack pack = new AvatarPack(
                new AvatarPack.Info(
                        Component.literal("skinmatics"),
                        Component.empty(),
                        "All Rights Reserved",
                        List.of(Minecraft.getInstance().getGameProfile().name()),
                        SkinmaticsMod.getVersionString()
                )
        );

        try {
            Files.createDirectories(AvatarTextureManager.PATH);

            try (Stream<Path> paths = Files.walk(AvatarTextureManager.PATH)) {
                paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".png"))
                        .forEach(path -> {
                            Path relative = AvatarTextureManager.PATH.relativize(path);
                            String stringPath = relative.toString().replace('\\', '/');
                            Identifier textureId = SkinmaticsMod.of(AvatarTextureManager.toTexturePath(stringPath));

                            try (InputStream stream = Files.newInputStream(path)) {
                                pack.registerTexture(new AvatarTextureData.Data(textureId, stringPath, NativeImage.read(stream)));
                            } catch (IOException e) {
                                SkinmaticsMod.LOGGER.error("[SKINMATICS PACK LOADER | EXTERNAL] Failed to load image: {}", AvatarTextureManager.PATH.relativize(path), e);
                            }
                        });
            }

            Files.createDirectories(AvatarDataManager.PATH);

            try (Stream<Path> paths = Files.walk(AvatarDataManager.PATH)) {
                paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            Path relative = AvatarDataManager.PATH.relativize(path);
                            String name = relative.toString().replace('\\', '/');
                            name = name.substring(0, name.endsWith(".json") ? name.length() - 5 : name.length());

                            try (Reader reader = Files.newBufferedReader(path)) {
                                JsonElement element = JsonConfigFormat.GSON.fromJson(reader, JsonObject.class);
                                ConfigResult<AvatarData, SimpleMetadataType.Metadata> result = ConfigManager.read(null, AvatarData.TYPE, element.getAsJsonObject());
                                pack.registerAvatar(name, Either.left(result.config()));
                            } catch (Exception e) {
                                SkinmaticsMod.LOGGER.warn("[SKINMATICS PACK LOADER | EXTERNAL] Failed to load {}:", name, e);
                                pack.registerAvatar(name, Either.right(e.toString()));
                            }

                        });
            }
        } catch (IOException e) {
            SkinmaticsMod.LOGGER.error("[SKINMATICS PACK LOADER | EXTERNAL] Failed to load client's pack", e);
        }

        avatarPacks.put("skinmatics", pack);
    }
}