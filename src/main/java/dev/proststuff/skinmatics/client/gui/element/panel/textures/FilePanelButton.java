package dev.proststuff.skinmatics.client.gui.element.panel.textures;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.client.texture.TextureData;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.panel.button.DescriptionPanelButton;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FilePanelButton extends DescriptionPanelButton {
    public static FilePanelButton TO_RENDER;
    public static FilePanelButton FOCUSED;

    protected final Map<String, FilePanelButton> children;
    protected FilePanelButton parent;
    protected final Type type;
    protected final TextureData textureData;

    public FilePanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> componentSupplier, @Nullable TextureData textureData) {
        super(identifier, componentSupplier);
        this.textureData = textureData;
        this.type = textureData != null ? Type.FILE : Type.DIRECTORY;
        this.children = isFile() ? null : new LinkedHashMap<>();
    }

    public FilePanelButton(SimpleIdentifier identifier, @Nullable TextureData textureData) {
        this(identifier, () -> Component.literal(identifier.path()), textureData);
    }

    public FilePanelButton(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    public FilePanelButton(String path, TextureData textureData) {
        this(Skinmatics.ofSimple(path), textureData);
    }

    public FilePanelButton(String path) {
        this(Skinmatics.ofSimple(path));
    }

    public boolean isFile() {
        return type == Type.FILE;
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
        children.put(filePanelButton.identifier.path(), filePanelButton);
        return this;
    }

    public void clear() {
        if (children != null) {
            for (FilePanelButton button : children.values()) {
                button.clear();
            }
            children.clear();
        }
    }

    public boolean isReturn() {
        return TO_RENDER == this;
    }

    @Override
    public MutableComponent getComponent() {
        return isReturn() ? Component.literal("...") : super.getComponent();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inside(event.x(), event.y())) {
            if (!isFile()) {
                if (FOCUSED == this) {
                    if (isReturn() && parent != null) {
                        TO_RENDER = parent;
                    } else {
                        TO_RENDER = this;
                    }
                } else {
                    FOCUSED = this;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        checkHover(mouseX, mouseY);
        renderBackground(graphics, FOCUSED == this ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor(0.5F));
        super.render(graphics, mouseX, mouseY, tickDelta);
    }

    public enum Type {
        FILE,
        DIRECTORY
    }
}
