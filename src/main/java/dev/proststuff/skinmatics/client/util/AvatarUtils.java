package dev.proststuff.skinmatics.client.util;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface AvatarUtils {
    String DIRECTORY = "avatars";

    static String toAvatarPath(String fileName, boolean withFileSuffix) {
        return DIRECTORY + "/" + fileName + (withFileSuffix ? ".json" : "");
    }

    static String fromPathIdentifier(SimpleIdentifier id) {
        return id.path().replace(DIRECTORY + "/", "");
    }

    static SimpleIdentifier toPathIdentifier(String fileName) {
        return SkinmaticsMod.ofSimple(toAvatarPath(fileName, false));
    }

    static @Nullable Entity getClientEntity() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity camera = minecraft.getCameraEntity();
        return camera != null ? camera : minecraft.player;
    }

    static @NonNull UUID getClientUUID() {
        Entity viewed = getClientEntity();

        if (viewed instanceof AbstractClientPlayer) {
            return viewed.getUUID();
        }

        return Minecraft.getInstance().getGameProfile().id();
    }

    static boolean isValidAvatarHolder(Entity entity) {
        return entity instanceof ClientAvatarEntity;
    }

    static boolean isClientEntity(Entity entity) {
        return isValidAvatarHolder(entity) && entity.getUUID().equals(getClientUUID());
    }
}