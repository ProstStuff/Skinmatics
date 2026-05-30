package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.SidebarButton;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SkinmaticsClientRegistration {
    public static final SkinmaticsClientRegistration INSTANCE = new SkinmaticsClientRegistration();

    private final Map<SimpleIdentifier, ButtonFactory<SidebarButton>> sidebarButtons = new LinkedHashMap<>();
    private final Map<String, Boolean> states = new LinkedHashMap<>();

    private SkinmaticsClientRegistration() {}

    public Map<SimpleIdentifier, ButtonFactory<SidebarButton>> getSidebarButtons() {
        return sidebarButtons;
    }

    public Map<String, Boolean> getStates() {
        return states;
    }

    public void addSidebarButton(SimpleIdentifier identifier, ButtonFactory<SidebarButton> buttonCreator) {
        if (sidebarButtons.put(identifier, buttonCreator) != null) {
            throw new IllegalStateException("Duplicate sidebar button ID: " + identifier);
        }
    }

    public boolean setState(String state, boolean value) {
        return Boolean.TRUE.equals(states.put(state, value));
    }

    public boolean hasState(String state) {
        return states.getOrDefault(state, false);
    }

    public interface ButtonFactory<B> {
        B create(SimpleIdentifier identifier);
    }
}