package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractSkinmaticsRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class AbstractPanelButton extends AbstractSkinmaticsRenderable implements ClickableRenderable {
    public static final int INDENT = 4;
    protected AbstractPanel panel = null;

    public AbstractPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    @Override
    public MutableComponent getComponent() {
        if (panel == null) return super.getComponent();
        return Component.translatable(getIdentifier().toLanguageKeyInfix(panel.getIdentifier().path()));
    }

    public void setPanel(AbstractPanel panel) {
        this.panel = panel;
    }

    public AbstractPanel getPanel() {
        return panel;
    }

    public int getHeightTextIndent() {
        return GraphicsUtils.font().lineHeight / 2;
    }

    @Override
    public int getWidth() {
        return AbstractPanel.WIDTH;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public String getCategory() {
        return "panel";
    }

    @Override
    public boolean in(double pointX, double pointY) {
        boolean insidePanel;

        if (panel != null) {
            insidePanel = pointX >= panel.getX() && pointY >= panel.getY() && pointX < panel.getX() + panel.getWidth() && pointY < panel.getY() + panel.getHeight();
        } else {
            insidePanel = false;
        }

        return super.in(pointX, pointY) && insidePanel;
    }
}
