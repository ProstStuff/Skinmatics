package dev.proststuff.skinmatics.client.gui.customization.panel;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.BooleanPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.GroupPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config.IntegerTextConfigPanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class CustomizePanel extends AbstractPanel {
    protected Profile profile;
    protected final BooleanPanelButton enabled = new BooleanPanelButton(SkinmaticsMod.ofSimple("enabled"), false);
    protected final IntegerTextConfigPanelButton maxTicks = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("max_ticks"));
    protected final BooleanPanelButton strongerEmissiveGlow = new BooleanPanelButton(SkinmaticsMod.ofSimple("stronger_emissive_glow"), false);

    protected final GroupPanelButton skin = new GroupPanelButton(SkinmaticsMod.ofSimple("skin"));
    protected final BooleanPanelButton slim = new BooleanPanelButton(SkinmaticsMod.ofSimple("slim"), false);
    protected final BooleanPanelButton useCustomSkin = new BooleanPanelButton(SkinmaticsMod.ofSimple("use_custom_skin"), false);
    protected final BooleanPanelButton addEmissiveSkin = new BooleanPanelButton(SkinmaticsMod.ofSimple("add_emissive_skin"), false);

    protected final GroupPanelButton capeAndWings = new GroupPanelButton(SkinmaticsMod.ofSimple("cape_and_wings"));
    protected final BooleanPanelButton showCape = new BooleanPanelButton(SkinmaticsMod.ofSimple("show_cape"), false);
    protected final BooleanPanelButton useCustomCape = new BooleanPanelButton(SkinmaticsMod.ofSimple("use_custom_cape"), false);
    protected final BooleanPanelButton addEmissiveCape = new BooleanPanelButton(SkinmaticsMod.ofSimple("add_emissive_cape"), false);
    protected final BooleanPanelButton useCustomWings = new BooleanPanelButton(SkinmaticsMod.ofSimple("use_custom_wings"), false);
    protected final BooleanPanelButton addEmissiveWings = new BooleanPanelButton(SkinmaticsMod.ofSimple("add_emissive_wings"), false);

    protected final GroupPanelButton eyes = new GroupPanelButton(SkinmaticsMod.ofSimple("eyes"));
    protected final BooleanPanelButton showEyes = new BooleanPanelButton(SkinmaticsMod.ofSimple("show_eyes"), false);
    protected final BooleanPanelButton addEmissiveEyes = new BooleanPanelButton(SkinmaticsMod.ofSimple("add_emissive_eyes"), false);
    protected final BooleanPanelButton hideRightEye = new BooleanPanelButton(SkinmaticsMod.ofSimple("hide_right_eye"), false);
    protected final BooleanPanelButton hideLeftEye = new BooleanPanelButton(SkinmaticsMod.ofSimple("hide_left_eye"), false);
    protected final IntegerTextConfigPanelButton blinkingChance = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("blinking_chance"));
    protected final IntegerTextConfigPanelButton holdBlinkingFor = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("hold_blinking_for"));

    public CustomizePanel() {
        super(SkinmaticsMod.ofSimple("customize"), false);
        addButtons(
                enabled,
                maxTicks,
                strongerEmissiveGlow,
                skin,
                capeAndWings,
                eyes
        );

        skin.addAll(
                slim,
                useCustomSkin,
                addEmissiveSkin
        );

        capeAndWings.addAll(
                showCape,
                useCustomCape,
                addEmissiveCape,
                useCustomWings,
                addEmissiveWings
        );

        eyes.addAll(
                showEyes,
                addEmissiveEyes,
                hideRightEye,
                hideLeftEye,
                blinkingChance,
                holdBlinkingFor
        );
    }

    @Override
    public MutableComponent getComponent() {
        Profile profile = ProfileManager.getCurrent();
        if (profile == null) return super.getComponent();
        return Component.translatable(identifier.toLanguageKey(), profile.getProfileName());
    }

    protected void updateFields() {
        Profile profile = ProfileManager.getCurrent();

        if (profile != null) {
            this.profile = profile;
            enabled.setField(profile.enabled);
            maxTicks.setField(profile.maxTicks);
            strongerEmissiveGlow.setField(profile.strongerEmissiveGlow);
            slim.setField(profile.slim);
            useCustomSkin.setField(profile.useCustomSkin);
            addEmissiveSkin.setField(profile.addEmissiveSkin);
            showCape.setField(profile.showCape);
            useCustomCape.setField(profile.useCustomCape);
            addEmissiveCape.setField(profile.addEmissiveCape);
            useCustomWings.setField(profile.useCustomWings);
            addEmissiveWings.setField(profile.addEmissiveWings);

            showEyes.setField(profile.showEyes);
            addEmissiveEyes.setField(profile.addEmissiveEyes);
            hideRightEye.setField(profile.hideRightEye);
            hideLeftEye.setField(profile.hideLeftEye);
            blinkingChance.setField(profile.blinkingChance);
            holdBlinkingFor.setField(profile.holdBlinkingFor);
        }
    }

    @Override
    public void init(int width, int height) {
        updateFields();
        super.init(width, height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (profile != ProfileManager.getCurrent()) {
            updateFields();
        }

        super.render(graphics, mouseX, mouseY, tickDelta);
    }
}