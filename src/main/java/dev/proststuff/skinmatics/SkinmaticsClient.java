package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.client.SkinmaticsClientEvents;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.skinmatics.PersistentSkinmaticsData;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinmaticsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SkinmaticsClient");

    public static SkinmaticsTextureManager TEXTURE_MANAGER;
    public static SkinmaticsConfig CONFIG;
    public static UUID LOCAL_UUID;
    public static PersistentSkinmaticsData LOCAL_SKINMATICS;
    public static final Map<UUID, SkinmaticsData> SKINMATICS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        SkinmaticsClientEvents.init();
    }

    public static void refreshConfig() {
        CONFIG = SkinmaticsConfig.load();
        CONFIG.save();
    }

    public static void refreshLocalSkinmatics() {
        LOCAL_SKINMATICS = PersistentSkinmaticsData.load();
        SkinmaticsData skinmaticsData = SKINMATICS.getOrDefault(LOCAL_UUID, null);

        if (skinmaticsData == null) {
            skinmaticsData = new SkinmaticsData();
            SKINMATICS.put(LOCAL_UUID, skinmaticsData);
        }

        LOCAL_SKINMATICS.apply(skinmaticsData);
        LOCAL_SKINMATICS.save();
    }

    public static @Nullable SkinmaticsData getSkinmaticsFromRenderState(PlayerEntityRenderState renderState) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return null;

        Entity entity = client.world.getEntityById(renderState.id);
        if (entity instanceof AbstractClientPlayerEntity player) {
            return SkinmaticsClient.SKINMATICS.get(player.getUuid());
        }

        return null;
    }

    public static Identifier getPlayerSkin(UUID uuid) {
        SkinmaticsData data = SkinmaticsClient.SKINMATICS.getOrDefault(uuid, null);

        if (data != null && data.validateSkin()) {
            return data.getSkinTexture();
        }

        return null;
    }
}
