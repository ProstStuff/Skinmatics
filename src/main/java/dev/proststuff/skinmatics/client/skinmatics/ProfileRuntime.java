package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.UUID;

public class ProfileRuntime {
    protected final Profile profile;
    protected final UUID UUID;
    protected final int id;

    protected int blinkingTicks = 0;
    protected EyeFeatureRenderer.EyeDirection rightEyeState = EyeFeatureRenderer.EyeDirection.FRONT;
    protected EyeFeatureRenderer.EyeDirection leftEyeState = EyeFeatureRenderer.EyeDirection.FRONT;

    public ProfileRuntime(Profile profile, UUID uuid, int id) {
        this.profile = profile;
        this.UUID = uuid;
        this.id = id;
    }

    public ProfileRuntime(Profile profile, Entity entity) {
        this(profile, entity.getUUID(), entity.getId());
    }

    public void tick(Entity entity, RandomSource random) {
        if (entity instanceof LivingEntity living) {
            if (profile.showEyes.get()) {
                if (blinkingTicks > 0) {
                    blinkingTicks --;
                }

                if (blinkingTicks == 0 && random.nextInt(profile.blinkingChance.get()) == 0) {
                    blink();
                }

                float yaw = Mth.wrapDegrees(entity.getYHeadRot() - living.yBodyRot);
                float pitch = entity.getXRot();

                EyeFeatureRenderer.EyeDirection eyeDirection = getEyeDirection(yaw, pitch, entity::hasPose);
                rightEyeState = eyeDirection;
                leftEyeState = eyeDirection;
            }
        }
    }

    public EyeFeatureRenderer.EyeDirection getEyeDirection(float yaw, float pitch, HasPose hasPose) {
        EyeFeatureRenderer.EyeDirection eyeDirectionState = EyeFeatureRenderer.EyeDirection.FRONT;

        if (blinkingTicks <= 0 && !hasPose.hasPose(Pose.SLEEPING)) {
            boolean up = pitch < -20;
            boolean down = pitch > 20;
            boolean right = yaw > 20;
            boolean left = yaw < -20;

            if (up && right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT_UP;
            else if (up && left) eyeDirectionState = EyeFeatureRenderer.EyeDirection.LEFT_UP;
            else if (down && right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT_DOWN;
            else if (down && left) eyeDirectionState = EyeFeatureRenderer.EyeDirection.LEFT_DOWN;
            else if (up) eyeDirectionState =  EyeFeatureRenderer.EyeDirection.UP;
            else if (down) eyeDirectionState =   EyeFeatureRenderer.EyeDirection.DOWN;
            else if (right) eyeDirectionState = EyeFeatureRenderer.EyeDirection.RIGHT;
            else if (left) eyeDirectionState =  EyeFeatureRenderer.EyeDirection.LEFT;
        } else {
            eyeDirectionState = EyeFeatureRenderer.EyeDirection.CLOSED;
        }

        return eyeDirectionState;
    }

    public int getEyeLayerStateOffset(EyeFeatureRenderer.EyePosition eyePosition, boolean emissive) {
        return ProfileData.getEyeLayerStateEmissive(emissive) + (eyePosition == EyeFeatureRenderer.EyePosition.RIGHT ? rightEyeState.ordinal() : leftEyeState.ordinal());
    }

    public void blink() {
        blinkingTicks = profile.holdBlinkingFor.get();
    }

    public interface HasPose {
        boolean hasPose(Pose pose);
    }
}
