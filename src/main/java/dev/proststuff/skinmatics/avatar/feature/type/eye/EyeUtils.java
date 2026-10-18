package dev.proststuff.skinmatics.avatar.feature.type.eye;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.Nullable;

public interface EyeUtils {
    String GROUP_EYES = "eyes";
    String GROUP_RIGHT = "right";
    String GROUP_LEFT = "left";

    String STATE_CLOSED = "closed";
    String STATE_FRONT = "front";
    String STATE_UP = "up";
    String STATE_DOWN = "down";

    String STATE_RIGHT = "right";
    String STATE_RIGHT_UP = "rightUp";
    String STATE_RIGHT_DOWN = "rightDown";

    String STATE_LEFT = "left";
    String STATE_LEFT_UP = "leftUp";
    String STATE_LEFT_DOWN = "leftDown";

    String STATE_IDLE = STATE_FRONT;
    
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
        return yaw <= -MIN_ROT;
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

        if (up && right) return STATE_RIGHT_UP;
        else if (up && left) return STATE_LEFT_UP;
        else if (down && right) return STATE_RIGHT_DOWN;
        else if (down && left) return STATE_LEFT_DOWN;
        else if (up) return STATE_UP;
        else if (down) return STATE_DOWN;
        else if (right) return STATE_RIGHT;
        else if (left) return STATE_LEFT;

        return STATE_IDLE;
    }

    static String getDynamicState(String group, LivingEntityRenderState state) {
        String result = evaluateState(group, state);
        if (result != null) return result;
        return getDirection(state.yRot, state.xRot);
    }

    static @Nullable String evaluateState(String group, LivingEntityRenderState state) {
        if (state.hasPose(Pose.SLEEPING) || state.hasPose(Pose.DYING)) return STATE_CLOSED;

        if (state instanceof ArmedEntityRenderState armed) {
            if (isAiming(armed.leftArmPose) && GROUP_RIGHT.equals(group)) {
                return STATE_CLOSED;
            } else if (isAiming(armed.rightArmPose) && GROUP_LEFT.equals(group)) {
                return STATE_CLOSED;
            }
        }

        return null;
    }

    static boolean isAiming(HumanoidModel.ArmPose pose) {
        return pose == HumanoidModel.ArmPose.BOW_AND_ARROW || pose == HumanoidModel.ArmPose.SPYGLASS || pose == HumanoidModel.ArmPose.THROW_TRIDENT;
    }
}