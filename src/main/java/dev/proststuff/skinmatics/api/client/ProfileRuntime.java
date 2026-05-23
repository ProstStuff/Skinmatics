package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.client.model.EyeRenderLayer;
import net.minecraft.util.Mth;
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
    protected final int id;

    protected @Nullable Profile profile;
    protected int blinkingTicks = 0;
    protected EyeRenderLayer.EyeDirection rightEyeState = EyeRenderLayer.EyeDirection.FRONT;
    protected EyeRenderLayer.EyeDirection leftEyeState = EyeRenderLayer.EyeDirection.FRONT;

    protected boolean shouldTickRightEye = true;
    protected boolean shouldTickLeftEye = true;

    public ProfileRuntime(UUID uuid, int id) {
        this.UUID = uuid;
        this.id = id;
    }

    public ProfileRuntime(Entity entity) {
        this(entity.getUUID(), entity.getId());
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

    public int getId() {
        return id;
    }

    public void tick(Entity entity, RandomSource random) {
        if (entity instanceof LivingEntity living) {
            if (profile != null && profile.showEyes.get()) {
                if (blinkingTicks > 0) {
                    blinkingTicks --;
                }

                if (blinkingTicks == 0 && random.nextInt(profile.blinkingChance.get()) == 0) {
                    blink();
                }

                float yaw = Mth.wrapDegrees(entity.getYHeadRot() - living.yBodyRot);
                float pitch = entity.getXRot();

                EyeRenderLayer.EyeDirection eyeDirection = getEyeDirection(yaw, pitch, entity::hasPose);
                if (shouldTickRightEye) rightEyeState = eyeDirection;
                if (shouldTickLeftEye) leftEyeState = eyeDirection;
            }
        }
    }

    public EyeRenderLayer.EyeDirection getEyeDirection(float yaw, float pitch, HasPose hasPose) {
        EyeRenderLayer.EyeDirection eyeDirectionState = EyeRenderLayer.EyeDirection.FRONT;

        if (blinkingTicks <= 0 && !hasPose.hasPose(Pose.SLEEPING)) {
            boolean up = pitch <= -22.5F;
            boolean down = pitch >= 22.5F;
            boolean right = yaw >= 22.5F;
            boolean left = yaw <= -22.5F;

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

    public int getEyeLayerStateOffset(EyeRenderLayer.EyePosition eyePosition, boolean emissive) {
        return ProfileData.getEyeLayerStateEmissive(emissive) + (eyePosition == EyeRenderLayer.EyePosition.RIGHT ? rightEyeState.ordinal() : leftEyeState.ordinal());
    }

    public void blink() {
        if (profile == null) return;
        blinkingTicks = profile.holdBlinkingFor.get();
    }

    public interface HasPose {
        boolean hasPose(Pose pose);
    }
}