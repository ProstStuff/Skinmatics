package com.fraynn.skinmatics.avatar;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import com.fraynn.utilitary.util.SimpleIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface AvatarUtils {
    String DIRECTORY = "a" + "vatars";

    static String toAvatarPath(String fileName, boolean withFileSuffix) {
        return DIRECTORY + "/" + fileName + (withFileSuffix ? ".json" : "");
    }

    static String fromPathIdentifier(SimpleIdentifier id) {
        return id.path().replace(DIRECTORY + "/", "");
    }

    static SimpleIdentifier toPathIdentifier(String fileName) {
        return Skinmatics.ofSimple(toAvatarPath(fileName, false));
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

    static RenderType getEmissiveRenderType(Identifier texture, boolean strongerEmissive) {
        return strongerEmissive || SkinmaticsConfig.getInstance().useStrongerEmissiveGlow() ? RenderTypes.eyes(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }
}