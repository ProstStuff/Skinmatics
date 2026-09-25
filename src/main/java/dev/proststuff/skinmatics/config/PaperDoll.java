package dev.proststuff.skinmatics.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;

public final class PaperDoll {
    static final PaperDoll INSTANCE = new PaperDoll();
    static final Codec<PaperDoll> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.BOOL.fieldOf("visible").forGetter(PaperDoll::isVisible),
                    Codec.BOOL.fieldOf("isAlwaysVisible").forGetter(PaperDoll::isAlwaysVisible),
                    Codec.INT.fieldOf("visibleTicks").forGetter(PaperDoll::getVisibleTicks),
                    Codec.BOOL.fieldOf("hideEquipments").forGetter(PaperDoll::shouldHideEquipments),
                    Codec.INT.fieldOf("scale").forGetter(PaperDoll::getScale),
                    Vec2.CODEC.fieldOf("screenAlignment").forGetter(PaperDoll::getScreenAlignment),
                    Vec2.CODEC.fieldOf("modelAlignment").forGetter(PaperDoll::getModelAlignment),
                    Vec2.CODEC.fieldOf("offset").forGetter(PaperDoll::getOffset)
            ).apply(inst, PaperDoll::apply)
    );

    private boolean visible = true;
    private boolean alwaysVisible = false;
    private int visibleTicks = 20;
    private boolean hideEquipments = false;
    private int scale = 24;
    private Vec2 screenAlignment = Vec2.ZERO;
    private Vec2 modelAlignment = Vec2.ZERO;
    private Vec2 offset = new Vec2(16.0F, 16.0F);

    private PaperDoll() {}

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    public void setVisibleTicks(int visibleTicks) {
        this.visibleTicks = visibleTicks;
    }

    public void setHideEquipments(boolean hideEquipments) {
        this.hideEquipments = hideEquipments;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setScreenAlignment(Vec2 screenAlignment) {
        this.screenAlignment = screenAlignment;
    }

    public void setModelAlignment(Vec2 modelAlignment) {
        this.modelAlignment = modelAlignment;
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public int getVisibleTicks() {
        return visibleTicks;
    }

    public boolean shouldHideEquipments() {
        return hideEquipments;
    }

    public int getScale() {
        return scale;
    }

    public Vec2 getScreenAlignment() {
        return screenAlignment;
    }

    public Vec2 getModelAlignment() {
        return modelAlignment;
    }

    public Vec2 getOffset() {
        return offset;
    }

    private static PaperDoll apply(boolean visible, boolean alwaysVisible, int visibleTicks, boolean hideEquipments, int scale, Vec2 screenAlignment, Vec2 modelAlignment, Vec2 offset) {
        INSTANCE.visible = visible;
        INSTANCE.alwaysVisible = alwaysVisible;
        INSTANCE.visibleTicks = visibleTicks;
        INSTANCE.hideEquipments = hideEquipments;
        INSTANCE.scale = scale;
        INSTANCE.screenAlignment = screenAlignment;
        INSTANCE.modelAlignment = modelAlignment;
        INSTANCE.offset = offset;

        return INSTANCE;
    }
}