package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.api.client.impl.SkinmaticsClientMod;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.client.gui.customization.avatar.AvatarGUIRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

// Somehow the only fix to arm model using the wrong model type...
public final class PaperDollRenderer implements HudElement, SkinmaticsClientMod {
    public static final PaperDollRenderer INSTANCE = new PaperDollRenderer();
    public static final Identifier IDENTIFIER = SkinmaticsMod.of("paper_doll");

    public static final int MODEL_BASE_WIDTH = 2;
    public static final int MODEL_BASE_HEIGHT = 3;

    private float yaw = 0.0F;
    private float lastStateYaw = 0.0F;
    private int tickCount = 0;
    private int showUntil = 0;
    private boolean preview = false;

    public PaperDollRenderer() {}

    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public AbstractClientPlayer getPlayer() {
        return preview ? AvatarGUIRenderer.getPreviewer() : getMinecraft().player;
    }

    public AvatarRenderState getRenderState(float deltaPartialTick) {
        AbstractClientPlayer player = getPlayer();

        if (player != null) {
            EntityRenderDispatcher dispatcher = getMinecraft().getEntityRenderDispatcher();
            AvatarRenderer<AbstractClientPlayer> renderer = dispatcher.getPlayerRenderer(player);
            AvatarRenderState state = renderer.createRenderState();
            renderer.extractRenderState(player, state, deltaPartialTick);

            if (!preview) {
                float deviation = config().paperDollYawDeviation.get();
                yaw += Mth.wrapDegrees(state.bodyRot - lastStateYaw);

                if (deviation < 180.0F) {
                    yaw = Mth.clamp(yaw, -deviation, deviation);
                }
            }

            lastStateYaw = state.bodyRot;
            state.bodyRot = yaw;

            return state;
        }

        return null;
    }

    public int getX() {
        int screenWidth = getMinecraft().getWindow().getGuiScaledWidth();
        int scale = config().paperDollScale.get();
        int width = getWidth(scale);

        boolean alternative = config().paperDollMirrorHorizontally.get();
        int x = alternative ? screenWidth - width : 0;
        int off = config().horizontalPaperDollOffset.get() + (preview ? 32 : 0);
        int offset = alternative ? -off : off;
        return x + offset;
    }

    public int getY() {
        int screenHeight = getMinecraft().getWindow().getGuiScaledHeight();
        int scale = config().paperDollScale.get();
        int height = getHeight(scale);

        boolean alternative = config().paperDollMirrorVertically.get();
        int y = alternative ? screenHeight - height : 0;
        int off = config().verticalPaperDollOffset.get() + (preview ? 32 : 0);
        int offset = alternative ? -off : off;
        return y + offset;
    }

    public int getWidth(int scale) {
        return MODEL_BASE_WIDTH * scale;
    }

    public int getHeight(int scale) {
        return MODEL_BASE_HEIGHT * scale;
    }

    public void showPaperDoll() {
        if (!preview) {
            this.showUntil = tickCount + config().paperDollShowFor.get();
        }
    }

    public void tick() {
        if (!preview) {
            tickCount ++;
        }
    }

    public void preview() {
        preview = true;
    }

    public void stopPreview() {
        preview = false;
        tickCount = 0;
        showUntil = config().paperDollShowFor.get();
    }

    public void render(GuiGraphicsExtractor graphics, float partialTick) {
        // Fix model
        boolean forceRender = preview || tickCount <= 20;

        if (forceRender || config().alwaysShowPaperDoll.get() || (config().showPaperDoll.get() && tickCount <= showUntil)) {
            AvatarRenderState state = getRenderState(partialTick);

            if (state != null) {
                double r = config().paperDollYawOffset.get();
                double rot = config().paperDollMirrorHorizontally.get() ? -r : r;

                Quaternionf rotation = new Quaternionf().rotateX((float) Math.PI).rotateY((float) Math.toRadians(rot));
                Vector3f translation = new Vector3f(0, AvatarGUIRenderer.ANCHOR_HEIGHT / 2.0F, 0).rotate(rotation);

                int scale = config().paperDollScale.get();
                int x = getX();
                int y = getY();
                int width = getWidth(scale);
                int height = getHeight(scale);

                // Prevents player model (that is in the state of not standing) to not be cut off by scissors
                int viewportLeft = x - scale * 4;
                int viewportTop = y - scale * 4;
                int viewportRight = x + width + scale * 4;
                int viewportBottom = y + height * 2 + scale * 4;

                graphics.entity(state, scale, translation, rotation, null, viewportLeft, viewportTop, viewportRight, viewportBottom);
            }
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        render(graphics, deltaTracker.getGameTimeDeltaPartialTick(true));
    }
}