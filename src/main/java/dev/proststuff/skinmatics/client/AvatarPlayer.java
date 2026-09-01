package dev.proststuff.skinmatics.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class AvatarPlayer extends RemotePlayer {
    public static final GameProfile DEFAULT_AVATAR_PROFILE = new GameProfile(UUID.randomUUID(), "");
    private static @Nullable AvatarPlayer INSTANCE = null;

    public AvatarPlayer(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Override
    public boolean isModelPartShown(@NonNull PlayerModelPart part) {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    public static boolean hasInstance() {
        return INSTANCE != null;
    }

    public static @NonNull AvatarPlayer getInstance() {
        Objects.requireNonNull(INSTANCE);
        return INSTANCE;
    }

    public static void createInstance() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (INSTANCE != null) throw new IllegalStateException("An existing AvatarPlayer need to be discarded before creating a new one");
        INSTANCE = new AvatarPlayer(minecraft.level, DEFAULT_AVATAR_PROFILE);

        if (minecraft.player != null) {
            INSTANCE.setItemInHand(InteractionHand.MAIN_HAND, minecraft.player.getMainHandItem().copy());
            INSTANCE.setItemInHand(InteractionHand.OFF_HAND, minecraft.player.getOffhandItem().copy());

            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                INSTANCE.setItemSlot(equipmentSlot, minecraft.player.getItemBySlot(equipmentSlot).copy());
            }

            INSTANCE.setPose(minecraft.player.getPose());
        }
    }

    public static void discardInstance() {
        if (INSTANCE != null) {
            INSTANCE.discard();
            INSTANCE = null;
        }
    }
}