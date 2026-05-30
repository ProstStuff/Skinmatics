package dev.proststuff.skinmatics.client.gui.customization.avatar;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractSkinmaticsRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.SidebarButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.AbstractPanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.impl.mixin.EntityPreviewer;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.skinmatics.client.gui.PaperDollRenderer;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class AvatarGUIRenderer extends AbstractSkinmaticsRenderable implements ClickableRenderable {
    public static final float ANCHOR_HEIGHT = 1.125F;
    protected static PreviewPlayer PREVIEWER = null;
    protected static TextureData IMAGE = null;
    
    protected final ModelPartHider modelPartHider = new ModelPartHider();
    protected final EyePreviewer eyePreviewer = new EyePreviewer();
    
    protected double pitch = 0;
    protected double yaw = 0;
    protected double offsetX = 0;
    protected double offsetY = 0;
    protected double scale = 64;

    protected boolean rotationDragging = false;
    protected boolean positionDragging = false;

    public AvatarGUIRenderer() {
        super(SkinmaticsMod.ofSimple("previewer"));
    }

    public AvatarRenderState getRenderState(float tickDelta) {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        eyePreviewer.update(PREVIEWER);
        AvatarRenderer<AbstractClientPlayer> renderer = dispatcher.getPlayerRenderer(PREVIEWER);
        AvatarRenderState state = renderer.createRenderState();
        ((EntityPreviewer) state).skinmatics$preview();
        renderer.extractRenderState(PREVIEWER, state, tickDelta);

        LocalPlayer player = minecraft.player;

        if (player != null) {
            state.headEquipment = player.getItemBySlot(EquipmentSlot.HEAD);
            state.chestEquipment = player.getItemBySlot(EquipmentSlot.CHEST);
            state.legsEquipment = player.getItemBySlot(EquipmentSlot.LEGS);
            state.feetEquipment = player.getItemBySlot(EquipmentSlot.FEET);
        }

        return state;
    }

    public void renderModel(GuiGraphicsExtractor graphics, float tickDelta) {
        AvatarRenderState state = getRenderState(tickDelta);

        Quaternionf rotation = new Quaternionf()
                .rotateY((float) Math.toRadians(yaw))
                .rotateX((float) (Math.toRadians(pitch) + Math.PI));
        Vector3f translation = new Vector3f(0, -ANCHOR_HEIGHT, 0)
                .rotate(rotation)
                .add((float) offsetX, (float) (offsetY), 0);

        graphics.entity(state, (float) scale, translation, rotation, null, getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }
    
    public void renderImage(GuiGraphicsExtractor graphics) {
        if (IMAGE == null) return;
        float twoF = 2.0F;
        float zoom = (float) scale;
        float downscaled = zoom / 32.0F;

        int scaledWidth = Math.round(IMAGE.width() * downscaled);
        int scaledHeight = Math.round(IMAGE.height() * downscaled);
        float centerX = getX() + getWidth() / twoF;
        float centerY = getY() + getHeight() / twoF;
        float drawX = centerX - scaledWidth / twoF + ((float) offsetX * zoom);
        float drawY = centerY - scaledHeight / twoF + ((float) offsetY * zoom);

        int tileSize = Math.max(1, Math.round(config().gridSize.get() * downscaled));
        int startX = Math.round(drawX);
        int startY = Math.round(drawY);

        int primaryGrid = config().primaryGridColor.get().get();
        int secondaryGrid = config().secondaryGridColor.get().get();

        graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        if (Math.max(IMAGE.width(), IMAGE.height()) <= 256) {
            graphics.enableScissor(startX, startY, startX + scaledWidth, startY + scaledHeight);

            for (int y = 0; y < scaledHeight; y += tileSize) {
                for (int x = 0; x < scaledWidth; x += tileSize) {
                    int color = ((x / tileSize) + (y / tileSize)) % 2 == 0 ? primaryGrid : secondaryGrid;
                    graphics.fill(startX + x, startY + y, startX + Math.min(x + tileSize, scaledWidth), startY + Math.min(y + tileSize, scaledHeight), color);
                }
            }

            graphics.disableScissor();
        }

        GraphicsUtils.sprite(
                graphics,
                IMAGE.textureIdentifier().identifier(),
                Math.round(drawX),
                Math.round(drawY),
                scaledWidth,
                scaledHeight
        );

        graphics.disableScissor();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        setPosition(SidebarButton.WIDTH + (isPanelVisible() ? getPanelWidth() : 0), getY());

        if (IMAGE != null) {
            renderImage(graphics);

            if (Math.max(IMAGE.width(), IMAGE.height()) >= 512) {
                GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.image_too_large"), 0.0F, getX() + getWidth() / 2, getY() + getHeight() - getHeight() / 16, foregroundColor());
            }
        } else {
            if (PREVIEWER == null) {
                GraphicsUtils.sprite(graphics, SkinmaticsAssets.ERROR, getX() + getWidth() / 2 - 32, getHeight() / 2 - 32, 64, 64);
                GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.player_error"), 1.0F, getX() + getWidth() / 2, getHeight() / 2 + 32, foregroundColor());
                return;
            }

            renderModel(graphics, tickDelta);
            modelPartHider.render(graphics, mouseX, mouseY, tickDelta);
            eyePreviewer.render(graphics, mouseX, mouseY, tickDelta);

            PaperDollRenderer.INSTANCE.render(graphics, tickDelta);
        }
    }

    @Override
    public void init(int width, int height) {
        PaperDollRenderer.INSTANCE.preview();
        super.init(width, height);
        IMAGE = null;
        modelPartHider.init(width, height);
        eyePreviewer.init(width, height);

        modelPartHider.setPosition(getScreenWidth() - modelPartHider.getWidth() / 2 - modelPartHider.getFigureWidth() - AbstractPanelButton.INDENT, modelPartHider.getFigureHeight() - modelPartHider.getHeight() / 2 + AbstractPanelButton.INDENT);
        eyePreviewer.setPosition(getScreenWidth() - eyePreviewer.getWidth() - AbstractPanelButton.INDENT, getScreenHeight() - eyePreviewer.getHeight() - AbstractPanelButton.INDENT);

        PreviewPlayer previewer = createPreviewer(Minecraft.getInstance().level);
        Profile current = ProfileManager.getCurrent();

        if (previewer != null && current != null) {
            setProfile(current);
        }
    }

    public boolean isPanelVisible() {
       return AbstractPanel.TOP_PANEL != null || AbstractPanel.BOTTOM_PANEL != null;
    }

    public int getPanelWidth() {
        int topWidth = (AbstractPanel.TOP_PANEL != null ? AbstractPanel.TOP_PANEL.getWidth() : 0);
        int bottomWidth = (AbstractPanel.BOTTOM_PANEL != null ? AbstractPanel.BOTTOM_PANEL.getWidth() : 0);
        return Math.max(topWidth, bottomWidth);
    }

    @Override
    public int getWidth() {
        return getScreenWidth() - (SidebarButton.WIDTH + getPanelWidth());
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (in(mouseX, mouseY)) {
            if (button == 0) {
                rotationDragging = true;
                return true;
            } else if (button == 1) {
                positionDragging = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (IMAGE == null) {
            if (modelPartHider.mouseClicked(event, doubleClick)) {
                return true;
            }

            if (eyePreviewer.mouseClicked(event, doubleClick)) {
                return true;
            }
        }

        return ClickableRenderable.super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (IMAGE == null) {
            if (eyePreviewer.mouseReleased(event)) {
                return true;
            }
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
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (IMAGE == null) {
            if (eyePreviewer.mouseDragged(event, dx, dy)) {
                return true;
            }
        }

        if (in(event.x(), event.y())) {
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
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (in(x, y)) {
            if (scrollY > 0) {
                scale *= 2;
            } else if (scrollY < 0) {
                scale /= 2;
            }

            scale = Mth.clamp(scale, 1.0, 256.0);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(double x, double y) {
        modelPartHider.mouseMoved(x, y);
        eyePreviewer.mouseMoved(x, y);

        return ClickableRenderable.super.mouseMoved(x, y);
    }

    @Override
    public int getHeight() {
        return getScreenHeight();
    }

    @Override
    public String getCategory() {
        return "avatar";
    }

    @Override
    public void onClose() {
        PaperDollRenderer.INSTANCE.stopPreview();
    }

    public static PreviewPlayer getPreviewer() {
        return PREVIEWER;
    }

    public static void setProfile(Profile profile) {
        if (PREVIEWER != null) {
            ProfileManager.assign(PREVIEWER, profile);
        }
    }

    public static void removePreviewer() {
        ProfileManager.unassign(AvatarGUIRenderer.getPreviewer());
        if (PREVIEWER != null) {
            PREVIEWER.remove(Entity.RemovalReason.DISCARDED);
        }

        PREVIEWER = null;
    }

    public static PreviewPlayer createPreviewer(ClientLevel level) {
        if (PREVIEWER != null) removePreviewer();
        if (level == null) return null;

        PREVIEWER = new PreviewPlayer(level, Minecraft.getInstance().getGameProfile());
        return PREVIEWER;
    }
    
    public static void setImage(TextureData data) {
        IMAGE = data;
    }
}
