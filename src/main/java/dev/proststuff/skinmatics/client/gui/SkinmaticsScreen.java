package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.nio.file.Path;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SkinmaticsScreen extends Screen {
    public static final int INSET = 16;
    public static final int UI_SIZE = 16;
    public static final int UI_SPACING = 8;
    public static final int ICON_SIZE = 16;
    public static final Identifier LIST = Skinmatics.of("textures/gui/misc.png");

    protected final Screen parent;
    protected final SkinmaticsTimeline timeline = new SkinmaticsTimeline();
    protected final SkinmaticsPreview preview = new SkinmaticsPreview();

    public SkinmaticsScreen(Screen parent) {
        super(Text.literal("skinmatics"));
        this.parent = parent;
    }

    public SkinmaticsConfig getConfig() {
        return SkinmaticsClient.CONFIG;
    }

    @Override
    protected void init() {
        SkinmaticsClient.refreshConfig();
        SkinmaticsClient.refreshLocalSkinmatics();
        timeline.load(SkinmaticsClient.LOCAL_SKINMATICS, SkinmaticsClient.LOCAL_SKINMATICS.cape);

        int left = INSET, right = width - INSET;
        int top = INSET, bottom = height - INSET;
        int x1 = left;
        int x2 = left + UI_SIZE;
        //TODO: Sidebar

        x1 = x2 + UI_SPACING;
        x2 += UI_SPACING + UI_SIZE * 6;
        //TODO: Property

        x1 = x2 + UI_SPACING;
        x2 = right - 66 - UI_SPACING;
        timeline.x = x1;
        timeline.y = bottom - (UI_SIZE * 2);
        timeline.width = x2 - timeline.x;
        timeline.height = bottom - timeline.y;
        timeline.load(SkinmaticsClient.LOCAL_SKINMATICS, SkinmaticsClient.LOCAL_SKINMATICS.emissiveCape);

        x1 = x2 + UI_SPACING;
        x2 = right;
        preview.x = x1;
        preview.y = bottom - 66;
        preview.width = x2 - preview.x;
        preview.height = 66;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int left = INSET, right = width - INSET;
        int top = INSET, bottom = height - INSET;

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            context.fill(left, top, right, bottom, ColorHelper.withAlpha(0.5F, getConfig().getBackgroundColor()));
            int color = ColorHelper.withAlpha(0.75F, getConfig().getForegroundColor());
            int w = width / 2;
            int h = height / 2;
            int f = textRenderer.fontHeight / 2;

            drawCenteredText(context, Text.translatable("skinmatics.screen.soon"), w, h - f, color);
            Path modPath = Path.of("config").resolve(Skinmatics.ID);

            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips1", getConfig().getProfile()), w, h + f + textRenderer.fontHeight, color);
            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips2", modPath.resolve("profiles").resolve(getConfig().getProfile() + ".json"), modPath.resolve("textures")), w, h + f * 2 + textRenderer.fontHeight * 2, color);
            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips3"), w, h + f * 3 + textRenderer.fontHeight * 3, color);
            return;
        }

        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(Skinmatics.ID);

        if (optional.isPresent()) {
            context.enableScissor(left, top, right, bottom);

            int x1 = left;
            int x2 = left + UI_SIZE;
            drawSidebar(context, mouseX, mouseY, deltaTicks, x1, top, x2 - x1, bottom - top);

            x1 = x2 + UI_SPACING;
            x2 += UI_SPACING + UI_SIZE * 6;
            drawProperties(context, mouseX, mouseY, deltaTicks, x1, top, x2 - x1, bottom - top);
            drawTimeline(context, mouseX, mouseY, deltaTicks);
            drawPreview(context, mouseX, mouseY, deltaTicks);

            context.disableScissor();
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    public void drawSidebar(DrawContext context, int mouseX, int mouseY, float deltaTick, int x, int y, int width, int height) {
        int background = getConfig().getBackgroundColor(0.5F);
        context.fill(x, y, x + width, y + height, background);
    }

    public void drawProperties(DrawContext context, int mouseX, int mouseY, float deltaTick, int x, int y, int width, int height) {
        int background = getConfig().getBackgroundColor(0.5F);
        context.fill(x, y, x + width, y + height, background);
    }

    public void drawTimeline(DrawContext context, int mouseX, int mouseY, float deltaTick) {
        int background = getConfig().getBackgroundColor(0.5F);
        int foreground = getConfig().getForegroundColor();

        int xLast = timeline.x + timeline.width;
        int yLast = timeline.y + timeline.height;

        context.enableScissor(timeline.x, timeline.y, xLast, yLast);
        context.fill(timeline.x, timeline.y, xLast, yLast, background);

        boolean inside = isMouseIn(mouseX, mouseY, timeline.x, timeline.y, timeline.width, timeline.height);
        boolean previewed = false;

        for (int i = 0; i <= timeline.persistentSkinmaticsData.maxTicks; i++) {

            if (i >= timeline.shownTicks) {
                int xPos = UI_SPACING + timeline.x + ((i - timeline.shownTicks) * 16);
                if (xPos > xLast - UI_SPACING) break;

                boolean hovered = isMouseIn(mouseX, mouseY, xPos - 8, timeline.y, 16, timeline.height);

                if (i % 20 == 0) {
                    context.fill(xPos, timeline.y, xPos + 1, timeline.y + timeline.height / 3, ColorHelper.withAlpha(0.25F, foreground));
                    drawCenteredText(context, Integer.toString(i), xPos, timeline.y + textRenderer.fontHeight / 3, hovered ? foreground : ColorHelper.withAlpha(0.75F, foreground));
                } else {
                    context.fill(xPos, timeline.y, xPos + 1, timeline.y + timeline.height / 4, ColorHelper.withAlpha(0.1F, foreground));
                    if (hovered) {
                        drawCenteredText(context, Integer.toString(i), xPos, timeline.y + textRenderer.fontHeight, foreground);
                    }
                }

                SkinmaticsTextureManager.SkinmaticsTextureData textureData = timeline.getTexture(i);

                if (textureData != null) {
                    context.fill(xPos - 3, timeline.y - 3 + timeline.height / 2, xPos + 3, timeline.y + 3 + timeline.height / 2, foreground);

                    if (inside) {
                        if (hovered) {
                            preview.textureData = textureData;
                            previewed = true;
                        } else if (!previewed) {
                            preview.textureData = null;
                        }
                    }
                }
            }
        }

        context.disableScissor();
    }

    public void drawPreview(DrawContext context, int mouseX, int mouseY, float deltaTick) {
        int background = getConfig().getBackgroundColor(0.5F);
        int foreground = getConfig().getForegroundColor();

        context.fill(preview.x, preview.y, preview.x + preview.width, preview.y + preview.height, background);

        if (preview.textureData != null) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, preview.textureData.textureId(), preview.x + 1, preview.y + 1, 0, 0, 64, 64, preview.textureData.width(), preview.textureData.height(), 0xFFFFFFFF);
        }

        if (isMouseIn(mouseX, mouseY, preview.x, preview.y, preview.width, preview.height)) {
            drawLeftAnchoredText(context, preview.textureData != null ? preview.textureData.texturePath() : "empty", preview.x + preview.width, preview.y - textRenderer.fontHeight, foreground);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseIn(mouseX, mouseY, timeline.x, timeline.y, timeline.width, timeline.height)) {
            timeline.shownTicks = MathHelper.clamp(timeline.shownTicks - (int) verticalAmount, 0, timeline.persistentSkinmaticsData.maxTicks);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        timeline.save();

        SkinmaticsClient.CONFIG.save();
        SkinmaticsClient.refreshConfig();
        SkinmaticsClient.LOCAL_SKINMATICS.save();
        SkinmaticsClient.refreshLocalSkinmatics();

        client.setScreen(parent);
    }

    public void drawText(DrawContext context, Text text, int x, int y, int color) {
        context.drawText(textRenderer, text, x, y, color, true);
    }

    public void drawText(DrawContext context, String text, int x, int y, int color) {
        context.drawText(textRenderer, text, x, y, color, true);
    }

    public void drawLeftAnchoredText(DrawContext context, Text text, int leftX, int y, int color) {
        drawText(context, text, leftX - textRenderer.getWidth(text), y, color);
    }

    public void drawLeftAnchoredText(DrawContext context, String text, int leftX, int y, int color) {
        drawText(context, text, leftX - textRenderer.getWidth(text), y, color);
    }

    public void drawCenteredText(DrawContext context, Text text, int centerX, int y, int color) {
        drawText(context, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }

    public void drawCenteredText(DrawContext context, String text, int centerX, int y, int color) {
        drawText(context, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }

    public static boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static boolean isMouseIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return isMouseIn((int) mouseX, (int) mouseY, x, y, width, height);
    }
}