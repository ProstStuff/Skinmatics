package dev.proststuff.skinmatics.client.gui.customization.avatar;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.Arrays;
import java.util.List;

public enum BodyPart {
    HEAD(-4, -8, 8, 8, EquipmentSlot.HEAD),
    TORSO(-4, 0, 8, 12, EquipmentSlot.CHEST),
    RIGHT_ARM(-8, -7, 0, 4, 12, 3, EquipmentSlot.CHEST),
    LEFT_ARM(4, 4, 0, 4, 12, 3, EquipmentSlot.CHEST),
    RIGHT_LEG(-4, 12, 4, 12, EquipmentSlot.LEGS, EquipmentSlot.FEET),
    LEFT_LEG(0, 12, 4, 12, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public final int offsetX;
    public final int slimOffsetX;
    public final int offsetY;
    public final int width;
    public final int slimWidth;
    public final int height;
    public final List<EquipmentSlot> slots;

    BodyPart(int offsetX, int slimOffsetX, int offsetY, int width, int height, int slimWidth, EquipmentSlot... slots) {
        this.offsetX = offsetX;
        this.slimOffsetX = slimOffsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.slimWidth = slimWidth;
        this.height = height;
        this.slots = Arrays.stream(slots).toList();
    }

    BodyPart(int offsetX, int offsetY, int width, int height, EquipmentSlot... slots) {
        this(offsetX, offsetX, offsetY, width, height, width, slots);
    }

    public boolean is(EquipmentSlot slot) {
        return slots.contains(slot);
    }

    public int getOffsetX(boolean slim) {
        return slim ? slimOffsetX : offsetX;
    }

    public int getWidth(boolean slim) {
        return slim ? slimWidth : width;
    }
}
