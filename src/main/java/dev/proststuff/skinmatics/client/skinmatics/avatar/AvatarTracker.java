package dev.proststuff.skinmatics.client.skinmatics.avatar;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.avatar.eyes.EyeStates;
import dev.proststuff.skinmatics.client.skinmatics.avatar.eyes.EyeUtils;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
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
        return eyeGroupStates.getOrDefault(group, state.skinmatics$eyesClosed() ? EyeStates.CLOSED : EyeUtils.getDynamicState(group, state));
    }

    public UUID getUUID() {
        return uuid;
    }

    public LivingEntity getEntity(Level level) {
        Entity entity = level.getEntity(uuid);
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
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