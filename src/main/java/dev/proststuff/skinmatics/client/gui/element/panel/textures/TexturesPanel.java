package dev.proststuff.skinmatics.client.gui.element.panel.textures;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.api.client.impl.gui.FileEventListener;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.element.panel.button.PanelButton;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TexturesPanel extends Panel implements FileEventListener {
    public static boolean RESCAN = false;

    protected FilePanelButton root = new FilePanelButton(Skinmatics.ofSimple("root"), null);

    public TexturesPanel() {
        super(Skinmatics.ofSimple("textures"));
    }

    public void scan() {
        FilePanelButton.TO_RENDER = root;
        SkinmaticsTextureManager.INSTANCE.reload();

        root.clear();

        for (Map.Entry<Identifier, TextureData> textureEntry : SkinmaticsTextureManager.INSTANCE.textures().entrySet()) {
            TextureData textureData = textureEntry.getValue();
            String[] path = textureData.texturePath().split("/");

            FilePanelButton filePanelButton = root;

            for (int i = 0; i < path.length; i++) {
                String part = path[i];

                if (i == path.length - 1) {
                    FilePanelButton file = new FilePanelButton(part, textureData);
                    filePanelButton.add(file);
                    file.init(screenWidth, screenHeight);
                } else {
                    filePanelButton = filePanelButton.get(part);
                    filePanelButton.init(screenWidth, screenHeight);
                }
            }
        }

        RESCAN = false;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        scan();
    }

    @Override
    public List<? extends PanelButton> getButtons() {
        FilePanelButton button = FilePanelButton.TO_RENDER;
        ArrayList<PanelButton> buttons = new ArrayList<>(button.children.values());

        if (button != root) {
            buttons.addFirst(button);
        }

        return buttons;
    }

    @Override
    public void onFilesDropped(@NonNull List<Path> files) {
       Path textureDir = FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("textures").resolve(SkinmaticsConfig.INSTANCE.profile.get());

        try {
            Files.createDirectories(textureDir);

            for (Path path : files) {
                File file = path.toFile();

                if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                    Path destination = textureDir.resolve(file.getName());
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            scan();
        } catch (Exception e) {
            SkinmaticsClient.LOGGER.error("Unable to copy file", e);
        }
    }

    @Override
    public void renderButtons(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, int startY) {
        super.renderButtons(graphics, mouseX, mouseY, tickDelta, startY);

        if (getButtons().isEmpty()) {
            int xRender = x + getWidth() / 2;
            int yRender = y + getHeight() / 2;
            GraphicsUtils.sprite(graphics, SkinmaticsAssets.ERROR,  xRender - 16, yRender - 16, 32, 32);
            GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.empty_textures"), 1.0F, xRender, yRender + 20, SkinmaticsConfig.foregroundColor());
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (RESCAN) scan();

        super.render(graphics, mouseX, mouseY, tickDelta);
    }
}
