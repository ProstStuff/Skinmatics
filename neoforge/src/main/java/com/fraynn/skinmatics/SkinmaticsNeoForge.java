package com.fraynn.skinmatics;


import com.fraynn.skinmatics.gui.screen.SkinmaticsGui;
import com.fraynn.utilitary.platform.NeoForgeClientHelper;
import com.fraynn.utilitary.platform.services.IClientHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Skinmatics.ID, dist = Dist.CLIENT)
public class SkinmaticsNeoForge {
    public SkinmaticsNeoForge(ModContainer modContainer, IEventBus eventBus) {
        Skinmatics.LOGGER.info("Initializing Skinmatics: NeoForge...");
        Skinmatics.init();
        ((NeoForgeClientHelper) IClientHelper.INSTANCE).registerFor(Skinmatics.ID, eventBus);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (_, parent) -> SkinmaticsGui.openScreen(parent));
    }
}