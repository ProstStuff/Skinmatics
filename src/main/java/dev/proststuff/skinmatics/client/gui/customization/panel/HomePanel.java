package dev.proststuff.skinmatics.client.gui.customization.panel;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.BooleanPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.GroupPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config.ColorTextConfigPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config.IntegerTextConfigPanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HomePanel extends AbstractPanel {
    protected final IntegerTextConfigPanelButton updateRange = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("update_range"));

    protected final GroupPanelButton paperDoll = new GroupPanelButton(SkinmaticsMod.ofSimple("paper_doll"));
    protected final BooleanPanelButton showPaperDoll = new BooleanPanelButton(SkinmaticsMod.ofSimple("show_paper_doll"), false);
    protected final BooleanPanelButton alwaysShowPaperDoll = new BooleanPanelButton(SkinmaticsMod.ofSimple("always_show_paper_doll"), false);
    protected final BooleanPanelButton paperDollHideEquipments = new BooleanPanelButton(SkinmaticsMod.ofSimple("paper_doll_hide_equipments"), false);
    protected final IntegerTextConfigPanelButton paperDollShowFor = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_show_for"));
    protected final IntegerTextConfigPanelButton paperDollScale = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_scale"));
    protected final IntegerTextConfigPanelButton paperDollYawDeviation = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_deviation"));
    protected final IntegerTextConfigPanelButton paperDollYawOffset = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_offset"));
    protected final BooleanPanelButton paperDollMirrorHorizontally = new BooleanPanelButton(SkinmaticsMod.ofSimple("paper_doll_mirror_horizontally"), false);
    protected final IntegerTextConfigPanelButton paperDollHorizontalOffset = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_horizontal_offset"));
    protected final BooleanPanelButton paperDollMirrorVertically = new BooleanPanelButton(SkinmaticsMod.ofSimple("paper_doll_mirror_vertically"), false);
    protected final IntegerTextConfigPanelButton paperDollVerticalOffset = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("paper_doll_vertical_offset"));

    protected final GroupPanelButton colors = new GroupPanelButton(SkinmaticsMod.ofSimple("colors"));
    protected final ColorTextConfigPanelButton backgroundColor = new ColorTextConfigPanelButton(SkinmaticsMod.ofSimple("background_color"));
    protected final ColorTextConfigPanelButton foregroundColor = new ColorTextConfigPanelButton(SkinmaticsMod.ofSimple("foreground_color"));
    protected final ColorTextConfigPanelButton accentColor = new ColorTextConfigPanelButton(SkinmaticsMod.ofSimple("accent_color"));

    protected final GroupPanelButton image = new GroupPanelButton(SkinmaticsMod.ofSimple("image"));
    protected final IntegerTextConfigPanelButton gridSize = new IntegerTextConfigPanelButton(SkinmaticsMod.ofSimple("grid_size"));
    protected final ColorTextConfigPanelButton primaryGridColor = new ColorTextConfigPanelButton(SkinmaticsMod.ofSimple("primary_grid_color"));
    protected final ColorTextConfigPanelButton secondaryGridColor = new ColorTextConfigPanelButton(SkinmaticsMod.ofSimple("secondary_grid_color"));

    public HomePanel() {
        super(SkinmaticsMod.ofSimple("home"), false);
        addButtons(updateRange, paperDoll, colors, image);

        paperDoll.addAll(
                showPaperDoll,
                alwaysShowPaperDoll,
                paperDollHideEquipments,
                paperDollShowFor,
                paperDollScale,
                paperDollYawDeviation,
                paperDollYawOffset,
                paperDollMirrorHorizontally,
                paperDollHorizontalOffset,
                paperDollMirrorVertically,
                paperDollVerticalOffset
        );

        colors.addAll(
                backgroundColor,
                foregroundColor,
                accentColor
        );

        image.addAll(
                gridSize,
                primaryGridColor,
                secondaryGridColor
        );

        updateRange.getTextbox().setUnit(Component.translatable("skinmatics.unit.blocks"));
        paperDollYawDeviation.getTextbox().setUnit(Component.translatable("skinmatics.unit.degrees"));
        paperDollYawOffset.getTextbox().setUnit(Component.translatable("skinmatics.unit.degrees"));
        paperDollShowFor.getTextbox().setUnit(Component.translatable("skinmatics.unit.ticks"));
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable("skinmatics.home", SkinmaticsMod.getVersion());
    }

    @Override
    public void init(int width, int height) {
        updateRange.setField(config().updateRange);

        showPaperDoll.setField(config().showPaperDoll);
        alwaysShowPaperDoll.setField(config().alwaysShowPaperDoll);
        paperDollHideEquipments.setField(config().paperDollHideEquipments);
        paperDollShowFor.setField(config().paperDollShowFor);
        paperDollScale.setField(config().paperDollScale);
        paperDollYawDeviation.setField(config().paperDollYawDeviation);
        paperDollYawOffset.setField(config().paperDollYawOffset);
        paperDollMirrorHorizontally.setField(config().paperDollMirrorHorizontally);
        paperDollHorizontalOffset.setField(config().horizontalPaperDollOffset);
        paperDollMirrorVertically.setField(config().paperDollMirrorVertically);
        paperDollVerticalOffset.setField(config().verticalPaperDollOffset);

        backgroundColor.setField(config().backgroundColor);
        foregroundColor.setField(config().foregroundColor);
        accentColor.setField(config().accentColor);

        gridSize.setField(config().gridSize);
        primaryGridColor.setField(config().primaryGridColor);
        secondaryGridColor.setField(config().secondaryGridColor);

        super.init(width, height);
    }
}
