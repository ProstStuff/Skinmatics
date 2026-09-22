package dev.proststuff.skinmatics.client.avatar;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.AvatarPlayer;
import dev.proststuff.skinmatics.client.avatar.data.AvatarData;
import dev.proststuff.skinmatics.client.util.EyeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class AvatarTracker {
    private final UUID uuid;

    private @NonNull AvatarData avatarData;
    private int blinkTicks;
    public int tickCount;

    private final Map<String, String> eyeGroupStates = new HashMap<>();

    public AvatarTracker(UUID uuid, @NonNull AvatarData avatarData) {
        this.uuid = uuid;
        this.avatarData = avatarData;
        this.tickCount = 0;
        this.blinkTicks = -avatarData.eyes().blinkingTicks();
    }

    public AvatarTracker(@NonNull Entity entity, @NonNull AvatarData avatarData) {
        this(entity.getUUID(), avatarData);
    }

    public void tick() {
        if (avatarData.eyes().blinkingChance() <= 0 || isBlinking()) return;
        if (SkinmaticsClient.RANDOM.nextInt(avatarData.eyes().blinkingChance()) == 0) blink(tickCount);
    }

    public String setEyeState(String group, String state) {
        return eyeGroupStates.put(group, state);
    }

    public String removeEyeState(String group) {
        return eyeGroupStates.remove(group);
    }

    public @NonNull String getEyeState(String group, AvatarRenderState state) {
        return eyeGroupStates.getOrDefault(group, state.skinmatics$eyesClosed() ? EyeUtils.STATE_CLOSED : EyeUtils.getDynamicState(group, state));
    }

    public UUID getUUID() {
        return uuid;
    }

    public @Nullable LivingEntity getEntity() {
        ClientLevel level = Minecraft.getInstance().level;
        Objects.requireNonNull(level);
        Entity entity = level.getEntity(uuid);

        if (uuid.equals(AvatarPlayer.DEFAULT_AVATAR_PROFILE.id())) return AvatarPlayer.getInstance();
        if (entity == null) return null;
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("AvatarTracker shouldn't track a non-living entity!");
        }

        return (LivingEntity) entity;
    }

    public void setAvatar(@NonNull AvatarData avatarData) {
        this.avatarData = avatarData;
        eyeGroupStates.clear();
    }

    public @NonNull AvatarData getAvatar() {
        return avatarData;
    }

    public void blink(int ticks) {
        this.blinkTicks = ticks;
    }

    public boolean isBlinking() {
        return tickCount < blinkTicks + avatarData.eyes().blinkingTicks();
    }
}