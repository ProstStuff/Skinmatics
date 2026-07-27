package dev.proststuff.skinmatics.client.skinmatics.profile;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.ProfileManager;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public interface ProfileUtils {
    static String toProfilePath(String fileName, boolean withFileSuffix) {
        return "profiles/" + fileName + (withFileSuffix ? ".json" : "");
    }

    static String toProfilePath(String fileName) {
        return toProfilePath(fileName, false);
    }

    static String fromSimpleIdentifier(SimpleIdentifier id) {
        return id.path().replace("profiles/", "");
    }

    static SimpleIdentifier toSimpleIdentifier(String fileName) {
        return SkinmaticsMod.ofSimple(toProfilePath(fileName));
    }

    static @Nullable Entity getViewingEntity() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();

        return camera != null ? camera : minecraft.player;
    }

    static @NonNull UUID getClientUUID() {
        return Minecraft.getInstance().getGameProfile().id();
    }

    static @NonNull UUID getViewingUUID() {
        Entity viewed = getViewingEntity();

        if (viewed instanceof AbstractClientPlayer) {
            return viewed.getUUID();
        }

        return getClientUUID();
    }

    static boolean isValidProfileHolder(Entity entity) {
        return entity instanceof ClientAvatarEntity;
    }

    static boolean isValidViewingEntity(Entity entity) {
        return isValidProfileHolder(entity) && entity.getUUID().equals(getViewingUUID());
    }

    static void createDirectory() {
        try {
            Files.createDirectories(ProfileManager.PATH);
        } catch (IOException e) {
            SkinmaticsMod.LOGGER.error("Failed to create profiles folder", e);
        }
    }
}