package dev.proststuff.skinmatics.client.skinmatics.profile;

import dev.proststuff.skinmatics.client.skinmatics.eyes.EyeStates;
import dev.proststuff.skinmatics.client.skinmatics.eyes.EyeUtils;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ProfileTracker {
    private final UUID uuid;

    private @NonNull Profile profile;
    private int lastBlinkedTick;

    private final Map<String, String> eyeGroupStates = new HashMap<>();

    public ProfileTracker(UUID uuid, int initialTickCount, @NonNull Profile profile) {
        this.uuid = uuid;
        this.lastBlinkedTick = initialTickCount;
        this.profile = profile;
    }

    public ProfileTracker(Entity entity, Profile profile) {
        this(entity.getUUID(), entity.tickCount, profile);
    }

    public String setEyeState(String group, String state) {
        return eyeGroupStates.put(group, state);
    }

    public String removeEyeState(String group) {
        return eyeGroupStates.remove(group);
    }

    public @NonNull String getEyeState(String group, LivingEntityRenderState renderState, boolean withBlink) {
        return eyeGroupStates.getOrDefault(group, withBlink && blinked((int) renderState.ageInTicks) ? EyeStates.CLOSED : EyeUtils.getDynamicState(group, renderState));
    }

    public UUID getUUID() {
        return uuid;
    }

    public Entity getEntity(Level level) {
        return level.getEntity(uuid);
    }

    public void setProfile(@NonNull Profile profile) {
        this.profile = profile;
        eyeGroupStates.clear();
    }

    public @NonNull Profile getProfile() {
        return profile;
    }

    public void blink(int ticks) {
        this.lastBlinkedTick = ticks;
    }

    public boolean blinked(int entityTickCount) {
        return lastBlinkedTick + profile.maxTicks() <= entityTickCount;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}