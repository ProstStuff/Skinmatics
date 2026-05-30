package dev.proststuff.skinmatics.client.gui.customization.panel;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.api.client.gui.impl.FileEventListener;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.FilePanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class TexturesPanel extends AbstractPanel implements FileEventListener {
    public static FilePanelButton TO_RENDER;

    protected final FilePanelButton root = new FilePanelButton(SkinmaticsMod.ofSimple(""));

    public TexturesPanel() {
        super(SkinmaticsMod.ofSimple("textures"), true);
        TO_RENDER = root;
    }

    public void scan() {
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
                    file.init(getScreenWidth(), getScreenHeight());
                } else {
                    filePanelButton = filePanelButton.get(part);
                    filePanelButton.init(getScreenWidth(), getScreenHeight());
                }
            }
        }
    }

    @Override
    public void init(int width, int height) {
        TO_RENDER = root;
        scan();
        super.init(width, height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        buttons.clear();

        if (TO_RENDER == null) {
            TO_RENDER = root;
        }

        if (TO_RENDER != root) {
            addButton(TO_RENDER);
        }

        for (FilePanelButton button : TO_RENDER.getChildren().values()) {
            FilePanelButton panelButton = addButton(button);
            panelButton.init(getScreenWidth(), getScreenHeight());
        }

        super.render(graphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public void onFilesDropped(@NonNull List<Path> files) {
        Path textureDir = TO_RENDER != null ? TO_RENDER.getPath() : FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("textures").resolve(SkinmaticsConfig.INSTANCE.profile.get());

        try {
            Files.createDirectories(textureDir);

            for (Path path : files) {
                File file = path.toFile();

                if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                    Path destination = textureDir.resolve(file.getName());
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            textureManager().reload();
            scan();
        } catch (Exception e) {
            SkinmaticsClient.LOGGER.error("Unable to copy file", e);
        }
    }
}