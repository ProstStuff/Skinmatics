package dev.proststuff.skinmatics.client.skinmatics.eyes;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;

public interface EyeUtils {
    float MIN_ROT = 22.5F;

    static boolean isUp(float pitch) {
        return pitch <= -MIN_ROT;
    }

    static boolean isDown(float pitch) {
        return pitch >= MIN_ROT;
    }


    static boolean isRight(float yaw) {
        return yaw >= MIN_ROT;
    }

    static boolean isLeft(float yaw) {
        return yaw <= MIN_ROT;
    }

    static float getPitch(LivingEntityRenderState state) {
        return state.yRot;
    }

    static float getYaw(LivingEntityRenderState state) {
        return state.xRot;
    }

    static String getDirection(float yaw, float pitch) {
        boolean up = isUp(pitch);
        boolean down = isDown(pitch);
        boolean right = isRight(yaw);
        boolean left = isLeft(yaw);

        if (up && right) return EyeStates.RIGHT_UP;
        else if (up && left) return EyeStates.LEFT_UP;
        else if (down && right) return EyeStates.RIGHT_DOWN;
        else if (down && left) return EyeStates.LEFT_DOWN;
        else if (up) return  EyeStates.UP;
        else if (down) return   EyeStates.DOWN;
        else if (right) return EyeStates.RIGHT;
        else if (left) return  EyeStates.LEFT;

        return EyeStates.IDLE;
    }

    static String getDynamicState(float yaw, float pitch) {
        return getDirection(yaw, pitch);
    }

    static String getDynamicState(String group, LivingEntityRenderState state) {
        String result = evaluateState(group, state);
        if (result != null) return result;
        return getDynamicState(state.yRot, state.xRot);
    }

    static @Nullable String evaluateState(String group, LivingEntityRenderState state) {
        if (state.hasPose(Pose.SLEEPING) || state.hasPose(Pose.DYING)) return EyeStates.CLOSED;

        if (state instanceof ArmedEntityRenderState armed) {
            if (isAiming(armed.leftArmPose) && EyeGroups.RIGHT.equals(group)) {
                return EyeStates.CLOSED;
            } else if (isAiming(armed.rightArmPose) && EyeGroups.LEFT.equals(group)) {
                return EyeStates.CLOSED;
            }
        }

        return null;
    }

    static boolean isAiming(HumanoidModel.ArmPose pose) {
        return pose == HumanoidModel.ArmPose.BOW_AND_ARROW || pose == HumanoidModel.ArmPose.SPYGLASS;
    }
}