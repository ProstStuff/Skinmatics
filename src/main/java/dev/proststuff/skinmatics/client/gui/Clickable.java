package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.client.gui.impl.Renderable;
import dev.proststuff.skinmatics.client.gui.impl.ScreenEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class Clickable implements ScreenEventListener, Renderable {
    private static final Map<ButtonType, Clickable> focused = new HashMap<>();
    private static final Map<ButtonType, Clickable> hovered = new HashMap<>();

    protected final Identifier identifier;
    protected final ButtonType type;

    public Clickable(Identifier identifier, ButtonType type) {
        this.identifier = identifier;
        this.type = type;
    }

    public void setFocused() {
        focused.put(type, this);
    }

    public void setHovered() {
        hovered.put(type, this);
    }

    public boolean isFocused() {
        return focused.containsValue(this);
    }

    public boolean isHovered() {
        return hovered.containsValue(this);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Component asComponent() {
        Identifier id = getIdentifier();
        return Component.translatable(id.getNamespace() + "." + type.name().toLowerCase(Locale.ROOT)  + "." + id.getPath());
    }

    public ButtonType getType() {
        return type;
    }

    public abstract int getHeight();

    @Override
    public int hashCode() {
        return identifier.hashCode() + type.hashCode();
    }

    public enum ButtonType {
        SIDEBAR,
        PANEL
    }

    public static Clickable getFocused(ButtonType type) {
        return focused.get(type);
    }

    public static Clickable getHovered(ButtonType type) {
        return hovered.get(type);
    }

    public static void unhover(ButtonType type) {
        hovered.remove(type);
    }
}
