package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.gui.customization.avatar.PreviewRenderer;
import dev.proststuff.skinmatics.client.gui.customization.panel.TexturesPanel;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilePanelButton extends TextPanelButton {
    protected final @Nullable TextureData textureData;
    protected final Map<String, FilePanelButton> children = new LinkedHashMap<>();
    protected FilePanelButton parent;

    public FilePanelButton(SimpleIdentifier identifier, @Nullable TextureData textureData) {
        super(identifier);
        this.textureData = textureData;
        textbox.setEditable(false);
        textbox.setContent(identifier.path());
    }

    public FilePanelButton(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    public FilePanelButton(String path, TextureData textureData) {
        this(SkinmaticsMod.ofSimple(path), textureData);
    }

    public FilePanelButton(String path) {
        this(SkinmaticsMod.ofSimple(path));
    }

    public String getFileName() {
        return identifier.path();
    }

    public String getDirectoryPath() {
        if (parent != null) {
            String parentDirectory = parent.getDirectoryPath();
            String parentPath = parent.getFileName();

            return parentDirectory + (!parentDirectory.isBlank() && !parentPath.isBlank() ? "/" : "") + parentPath;
        }

        return getFileName();
    }

    public Path getPath() {
        return FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("textures").resolve(getDirectoryPath()).resolve(getFileName());
    }

    public Map<String, FilePanelButton> getChildren() {
        return children;
    }

    public boolean isFile() {
        return textureData != null;
    }

    public FilePanelButton get(String path) {
        if (isFile()) {
            throw new UnsupportedOperationException("This FilePanelButton is in FILE mode");
        }

        return children.computeIfAbsent(path, (p) -> {
            FilePanelButton panelButton = new FilePanelButton(p);
            panelButton.parent = this;
            return panelButton;
        });
    }

    public FilePanelButton add(FilePanelButton filePanelButton) {
        children.put(filePanelButton.getIdentifier().path(), filePanelButton);
        filePanelButton.parent = this;
        return this;
    }

    public void clear() {
        for (FilePanelButton button : children.values()) {
            button.clear();
        }

        children.clear();
    }

    public boolean isReturn() {
        return TexturesPanel.TO_RENDER == this;
    }

    @Override
    public boolean isFocused() {
        return isFile() && super.isFocused();
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            if (isFocused()) {
                PreviewRenderer.setImage(null);
                return false;
            }

            PreviewRenderer.setImage(textureData);

            if (!isFile()) {
                if (isReturn()) {
                    TexturesPanel.TO_RENDER = this.parent;
                    textbox.setContent(identifier.path());
                } else {
                    TexturesPanel.TO_RENDER = this;
                    textbox.setContent("...");
                }
            }

            return true;
        }

        return isFocused();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderClickableState(graphics);
        textbox.render(graphics, foregroundColor());
    }
}