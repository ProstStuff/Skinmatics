package dev.proststuff.skinmatics.client.gui.element.avatar;

import com.mojang.authlib.GameProfile;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.impl.EntityPreviewer;
import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.element.sidebar.Sidebar;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class AvatarPreviewRenderer extends AbstractRenderable implements MouseEventListener {
    public static final float ANCHOR_HEIGHT = 1.125F;
    public static RemotePlayer PREVIEWER;
    public static Profile PROFILE_PREVIEW = null;

    protected final AvatarMinimap minimap = new AvatarMinimap();
    protected final HeadRotationPreviewer headRotation = new HeadRotationPreviewer();

    protected double pitch = 0;
    protected double yaw = 0;
    protected double offsetX = 0;
    protected double offsetY = 0;
    protected double scale = 64;

    protected boolean rotationDragging = false;
    protected boolean positionDragging = false;

    public AvatarPreviewRenderer() {
        super(Skinmatics.of("previewer"));
    }

    public static void setUpAvatar(ClientLevel level) {
        if (PREVIEWER != null) {
            PREVIEWER.remove(Entity.RemovalReason.DISCARDED);
        }

        GameProfile profile = Minecraft.getInstance().getGameProfile();
        PREVIEWER = new RemotePlayer(level, profile);
        ((EntityPreviewer) PREVIEWER).skinmatics$preview();
        PREVIEWER.setPos(0, 0, 0);
        PREVIEWER.setUUID(UUID.randomUUID());

        if (PROFILE_PREVIEW == null) {
            PROFILE_PREVIEW = ProfileManager.getCurrent();
        }

        changeProfile(PROFILE_PREVIEW);
    }

    public static void equipElytra(boolean equipped) {
        if (PREVIEWER != null) {
            if (equipped) {
                PREVIEWER.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            } else {
                PREVIEWER.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            }
        }
    }

    public static void changeProfile(Profile profile) {
        PROFILE_PREVIEW = profile;

        if (PREVIEWER != null) {
            ProfileManager.assign(PREVIEWER, profile);
        }
    }

    public AvatarRenderState getRenderState(float tickDelta) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        AvatarRenderer<AbstractClientPlayer> renderer = dispatcher.getPlayerRenderer(PREVIEWER);
        AvatarRenderState state = renderer.createRenderState();
        ((EntityPreviewer) state).skinmatics$preview();
        renderer.extractRenderState(PREVIEWER, state, tickDelta);
        return state;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        minimap.init(width, height);
        headRotation.init(width, height);

        minimap.setPosition(screenWidth - minimap.getWidth() - 16, 16);
        headRotation.setPosition(screenWidth - headRotation.getWidth() - 16, screenHeight - headRotation.getHeight() - 16);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (minimap.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (headRotation.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (inside(event.x(), event.y())) {
            if (event.button() == 0) {
                rotationDragging = true;
                return true;
            } else if (event.button() == 1) {
                positionDragging = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (headRotation.mouseDragged(event, dx, dy)) {
            return true;
        }

        if (inside(event.x(), event.y())) {
            if (rotationDragging && event.button() == 0) {
                yaw -= dx;
                pitch += dy;

                yaw = Mth.wrapDegrees(yaw);
                pitch = Mth.wrapDegrees(pitch);
                return true;
            } else if (positionDragging && event.button() == 1) {
                offsetX += dx / scale;
                offsetY += dy / scale;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (headRotation.mouseReleased(event)) {
            return true;
        }

        if (event.button() == 0 && rotationDragging) {
            rotationDragging = false;
            return true;
        }

        if (event.button() == 1 && positionDragging) {
            positionDragging = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (inside(x, y)) {
            scale += scrollY * 2.0;
            scale = Mth.clamp(scale, 1.0, 256.0);
        }

        return false;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (PREVIEWER == null) {
            GraphicsUtils.sprite(graphics, SkinmaticsAssets.ERROR, screenWidth / 2 - 32, screenHeight / 2 - 32, 64, 64);
            GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.player_error"), 1.0F, screenWidth / 2, screenHeight / 2 + 32, SkinmaticsConfig.foregroundColor());
            return;
        }

        headRotation.updateValues(PREVIEWER);
        renderModel(graphics, tickDelta);
        minimap.render(graphics, mouseX, mouseY, tickDelta);
        headRotation.render(graphics, mouseX, mouseY, tickDelta);
    }

    public void renderModel(GuiGraphicsExtractor graphics, float tickDelta) {
        AvatarRenderState state = getRenderState(tickDelta);

        Quaternionf rotation = new Quaternionf()
                .rotateY((float) Math.toRadians(yaw))
                .rotateX((float) (Math.toRadians(pitch) + Math.PI));
        Vector3f translation = new Vector3f(0, -ANCHOR_HEIGHT, 0)
                .rotate(rotation)
                .add((float) offsetX, (float) (offsetY), 0);

        graphics.entity(state, (float) scale, translation, rotation, null, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public int getWidth() {
        return screenWidth - Sidebar.SIDEBAR_WIDTH - Panel.WIDTH;
    }

    @Override
    public int getHeight() {
        return screenHeight;
    }
}