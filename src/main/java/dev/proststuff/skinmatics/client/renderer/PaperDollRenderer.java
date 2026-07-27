package dev.proststuff.skinmatics.client.renderer;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.gui.SkinmaticsUIElement;
import dev.proststuff.skinmatics.client.skinmatics.config.PaperDoll;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class PaperDollRenderer implements HudElement, SkinmaticsUIElement {
    public static final PaperDollRenderer INSTANCE = new PaperDollRenderer();

    public static final int PLAYER_HEIGHT = 2;
    public static final int PLAYER_WIDTH = 1;
    public static final Identifier ID = SkinmaticsMod.of("paper_doll");

    protected boolean forceEnabled = false;
    protected int visibleTicks = 0;

    public PaperDollRenderer() {}

    public PaperDoll paperDoll() {
        return SkinmaticsConfig.getInstance().getPaperDoll();
    }

    public boolean alwaysVisible() {
        return paperDoll().isAlwaysVisible();
    }

    public boolean isEnabled() {
        return forceEnabled || paperDoll().isAlwaysVisible();
    }

    public boolean isVisible() {
        return isEnabled() && (visibleTicks > 0 || alwaysVisible());
    }

    public Entity getEntity() {
        return minecraft().getCameraEntity();
    }

    public void visibleFor(int ticks) {
        this.visibleTicks = ticks;
    }

    public void tick() {
        if (isVisible()) {
            visibleTicks = Math.max(0, visibleTicks - 1);
        }

        if (!alwaysVisible()) {
            Entity entity = getEntity();

            if (entity != null && (!entity.hasPose(Pose.STANDING) || entity.isSprinting())) {
                visibleFor(paperDoll().getVisibleTicks());
            }
        }
    }

    public void modifyRenderState(EntityRenderState state, Entity entity, float partialTicks) {
        boolean hideEquipments = paperDoll().shouldHideEquipments();

        if (hideEquipments) {
            if (state instanceof ArmedEntityRenderState armed) {
                armed.rightHandItemStack = ItemStack.EMPTY;
                armed.leftHandItemStack = ItemStack.EMPTY;
            }

            if (state instanceof HumanoidRenderState humanoid) {
                humanoid.headEquipment = ItemStack.EMPTY;
                humanoid.chestEquipment = ItemStack.EMPTY;
                humanoid.legsEquipment = ItemStack.EMPTY;
                humanoid.feetEquipment = ItemStack.EMPTY;
            }
        }

        if (state instanceof LivingEntityRenderState renderState && entity instanceof LivingEntity living) {
            renderState.bodyRot = 0;
        }
    }

    public void render(@NonNull Entity entity, @NonNull GuiGraphicsExtractor graphics, float tickDelta) {
        if (!isVisible()) return;
        EntityRenderState state = minecraft().getEntityRenderDispatcher().extractEntity(entity, tickDelta);

        float entityScale = state instanceof LivingEntityRenderState living ? living.scale : 1.0F;
        float scale = paperDoll().getScale() * entityScale;
        int modelWidth = Math.round(scale * PLAYER_WIDTH) * 2;
        int modelHeight = Math.round(scale * PLAYER_HEIGHT);

        Vec2 screenAlignment = paperDoll().getScreenAlignment();
        Vec2 modelAlignment = paperDoll().getModelAlignment();
        Vec2 modelOffset = paperDoll().getOffset();

        float screenX = graphics.guiWidth() * screenAlignment.x;
        float screenY = graphics.guiHeight() * screenAlignment.y;
        float anchorX = modelWidth * modelAlignment.x;
        float anchorY = modelHeight * modelAlignment.y;
        int x0 = Math.round(screenX - anchorX + modelOffset.x);
        int y0 = Math.round(screenY - anchorY + modelOffset.y);

        float offsetX = (x0 - graphics.guiWidth() / 2F + modelWidth / 2F) / scale;
        float offsetY = (y0 - graphics.guiHeight() / 2F + modelHeight / 2F) / scale;

        modifyRenderState(state, entity, tickDelta);
        Vector3f position = new Vector3f();
        Quaternionf rotation = new Quaternionf().rotateX((float) Math.PI).rotateY((float) (Math.PI / 8.0) * (1.0F - screenAlignment.x * 2.0F));
        position.add(0, PLAYER_HEIGHT * entityScale / 2.0F, 0)
                .add(offsetX, offsetY, 0)
        ;

        graphics.entity(state, scale, position, rotation, null, 0, 0, graphics.guiWidth(), graphics.guiHeight());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Entity entity = getEntity();
        if (entity == null) return;

        if (!entity.hasPose(Pose.STANDING) || entity.isSprinting()) visibleFor(paperDoll().getVisibleTicks());
        render(entity, graphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}