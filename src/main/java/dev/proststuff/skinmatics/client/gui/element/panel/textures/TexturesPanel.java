package dev.proststuff.skinmatics.client.gui.element.panel.textures;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.api.client.impl.gui.FileEventListener;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.element.panel.button.PanelButton;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TexturesPanel extends Panel implements FileEventListener {
    protected FilePanelButton root = new FilePanelButton(Skinmatics.ofSimple("root"), null);

    public TexturesPanel() {
        super(Skinmatics.ofSimple("textures"));
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        FilePanelButton.TO_RENDER = root;
        root.clear();

        for (Map.Entry<Identifier, TextureData> textureEntry : SkinmaticsClient.TEXTURE_MANAGER.textures().entrySet()) {
            TextureData textureData = textureEntry.getValue();
            String[] path = textureData.texturePath().split("/");

            FilePanelButton filePanelButton = root;

            for (int i = 0; i < path.length; i++) {
                String part = path[i];

                if (i == path.length - 1) {
                    FilePanelButton file = new FilePanelButton(part, textureData);
                    filePanelButton.add(file);
                    file.init(width, height);
                } else {
                    filePanelButton = filePanelButton.get(part);
                    filePanelButton.init(width, height);
                }
            }
        }
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
        for (Path path : files) {
            SkinmaticsClient.LOGGER.info(path.toString());
        }
    }
}
