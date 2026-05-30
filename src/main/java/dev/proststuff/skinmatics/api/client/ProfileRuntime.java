package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.api.client.impl.mixin.UIRendered;
import dev.proststuff.skinmatics.client.render.EyeRenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Store entity related data. Exists per loaded (assigned) entity, removed when unloaded.
 */
public class ProfileRuntime {
    protected final UUID UUID;

    protected @Nullable Profile profile;
    protected int blinkingTicks = 0;

    // Unused atm, might reuse it for locked eye state
    protected EyeRenderLayer.EyeDirection rightEyeState = EyeRenderLayer.EyeDirection.FRONT;
    protected EyeRenderLayer.EyeDirection leftEyeState = EyeRenderLayer.EyeDirection.FRONT;
    protected boolean shouldTickRightEye = true;
    protected boolean shouldTickLeftEye = true;

    public ProfileRuntime(UUID uuid) {
        this.UUID = uuid;
    }

    public boolean isBlinking() {
        return blinkingTicks > 0;
    }

    public void setProfile(@Nullable Profile profile) {
        this.profile = profile;
        blinkingTicks = 0;
    }

    public @Nullable Profile getProfile() {
        return profile;
    }

    public UUID getUUID() {
        return UUID;
    }

    public void tick(Entity entity, RandomSource random) {
        if (entity instanceof LivingEntity) {
            if (profile != null && profile.showEyes.get()) {
                if (blinkingTicks > 0) {
                    blinkingTicks --;
                }

                int blinkingChance = profile.blinkingChance.get();
                if (blinkingTicks == 0 && blinkingChance > 0 && random.nextInt(profile.blinkingChance.get()) == 0) {
                    blink();
                }
            }
        }
    }

    public static EyeRenderLayer.EyeDirection getEyeDirection(float yaw, float pitch, boolean isBlinking, HasPose hasPose, boolean isIngameUI) {
        EyeRenderLayer.EyeDirection eyeDirectionState = EyeRenderLayer.EyeDirection.FRONT;
        // For UI usage (Inventory eye more responsive)
        float MIN_ROT = 22.5F / (isIngameUI ? 3 : 1);

        if (!isBlinking && !hasPose.hasPose(Pose.SLEEPING)) {
            boolean up = pitch <= -MIN_ROT;
            boolean down = pitch >= MIN_ROT;
            boolean right = yaw >= MIN_ROT;
            boolean left = yaw <= -MIN_ROT;

            if (up && right) eyeDirectionState = EyeRenderLayer.EyeDirection.RIGHT_UP;
            else if (up && left) eyeDirectionState = EyeRenderLayer.EyeDirection.LEFT_UP;
            else if (down && right) eyeDirectionState = EyeRenderLayer.EyeDirection.RIGHT_DOWN;
            else if (down && left) eyeDirectionState = EyeRenderLayer.EyeDirection.LEFT_DOWN;
            else if (up) eyeDirectionState =  EyeRenderLayer.EyeDirection.UP;
            else if (down) eyeDirectionState =   EyeRenderLayer.EyeDirection.DOWN;
            else if (right) eyeDirectionState = EyeRenderLayer.EyeDirection.RIGHT;
            else if (left) eyeDirectionState =  EyeRenderLayer.EyeDirection.LEFT;
        } else {
            eyeDirectionState = EyeRenderLayer.EyeDirection.CLOSED;
        }

        return eyeDirectionState;
    }

    public void blink() {
        if (profile == null) return;
        blinkingTicks = profile.holdBlinkingFor.get();
    }

    public static int getEyeState(float yaw, float pitch, boolean isBlinking, HasPose hasPose, boolean halved) {
        return getEyeDirection(yaw, pitch, isBlinking, hasPose, halved).ordinal();
    }

    public static int getEyeState(AvatarRenderState state, boolean isBlinking, HasPose hasPose) {
        // DEV NOTE: state.yRot is already a normalized (getYHeadRot() - getBodyRot())
        return getEyeState(state.yRot, state.xRot, isBlinking, hasPose, ((UIRendered) state).skinmatics$isUiRendered());
    }

    public interface HasPose {
        HasPose EMPTY = (_) -> false;
        boolean hasPose(Pose pose);
    }
}