package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

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
    protected final GameOptions gameOptions = MinecraftClient.getInstance().options;

    public SkinmaticsScreen(Screen parent) {
        super(Text.literal("skinmatics"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SkinmaticsClient.refreshConfig();
        SkinmaticsClient.refreshLocalSkinmatics();

        int left = INSET, right = width - INSET;
        int top = INSET, bottom = height - INSET;

        addDrawableChild(SkinmaticsButtonWidget.createIcon(left, top, LIST, ICON_SIZE, ICON_SIZE, Text.literal("list"), button -> {
            if (button instanceof SkinmaticsButtonWidget buttonWidget) {
                buttonWidget.toggle();
            }
        }));

        //addDrawableChild(SkinmaticsButtonWidget.createText(right - UI_SIZE, bottom - (UI_HEIGHT * 2) - (UI_HEIGHT / 4), Text.literal("Close"), UI_SIZE, UI_HEIGHT, button -> close()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int left = INSET, right = width - INSET;
        int top = INSET, bottom = height - INSET;

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            context.fill(left, top, right, bottom, SkinmaticsClient.CONFIG.getBackgroundColor());
            int color = ColorHelper.withAlpha(0.75F, SkinmaticsClient.CONFIG.getForegroundColor());
            int w = width / 2;
            int h = height / 2;
            int f = textRenderer.fontHeight / 2;

            drawCenteredText(context, Text.translatable("skinmatics.screen.soon"), w, h - f, ColorHelper.withAlpha(1.0F, color));
            Path modPath = Path.of("config").resolve(Skinmatics.ID);

            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips1", modPath.resolve("config.json")), w, h + f + textRenderer.fontHeight, color);
            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips2", modPath.resolve("profiles").resolve(SkinmaticsClient.CONFIG.getCurrentSkinmaticsProfile() + ".json"), modPath.resolve("textures")), w, h + f * 2 + textRenderer.fontHeight * 2, color);
            drawCenteredText(context, Text.translatable("skinmatics.screen.soon_tips3"), w, h + f * 3 + textRenderer.fontHeight * 3, color);
            return;
        }

        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(Skinmatics.ID);

        if (optional.isPresent()) {
            context.enableScissor(left, top, right, bottom);

            ModContainer modContainer = optional.get();
            ModMetadata modMetadata = modContainer.getMetadata();

            SkinmaticsConfig skinmaticsConfig = SkinmaticsClient.CONFIG;
            int bgColor = skinmaticsConfig.getBackgroundColor();
            int fgColor = skinmaticsConfig.getForegroundColor();
            int accentColor = skinmaticsConfig.getAccentColor();

            int x1 = left;
            int x2 = left + UI_SIZE;
            context.fill(x1, top, x2, bottom, bgColor);

            x1 = x2 + UI_SPACING;
            x2 += UI_SPACING + UI_SIZE * 6;
            context.fill(x1, top, x2, bottom, bgColor);

            x1 = x2 + UI_SPACING;
            x2 = right - 66 - UI_SPACING;
            drawText(context, Text.literal("Skinmatics " + modMetadata.getVersion().getFriendlyString()), x1, top, accentColor);
            context.fill(x1, bottom - (UI_SIZE * 5), x2, bottom, bgColor);

            x1 = x2 + UI_SPACING;
            x2 = right;
            context.fill(x1, bottom - 66, x2, bottom, bgColor);

            context.disableScissor();
        }
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        SkinmaticsClient.CONFIG.save();
        SkinmaticsClient.refreshConfig();
        SkinmaticsClient.LOCAL_SKINMATICS.save();
        SkinmaticsClient.refreshLocalSkinmatics();
    }

    public void drawText(DrawContext context, Text text, int x, int y, int color) {
        context.drawText(textRenderer, text, x, y, color, true);
    }

    public void drawLeftAnchoredText(DrawContext context, Text text, int leftX, int y, int color) {
        drawText(context, text, leftX - textRenderer.getWidth(text), y, color);
    }

    public void drawCenteredText(DrawContext context, Text text, int centerX, int y, int color) {
        drawText(context, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }
}