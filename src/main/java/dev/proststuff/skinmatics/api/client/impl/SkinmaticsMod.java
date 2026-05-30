package dev.proststuff.skinmatics.api.client.impl;

import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public interface SkinmaticsMod {
    String ID = "skinmatics";
    Logger LOGGER = LoggerFactory.getLogger(ID);

    static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    static SimpleIdentifier ofSimple(String path) {
        return SimpleIdentifier.of(ID, path);
    }

    static String getVersion() {
        AtomicReference<String> version = new AtomicReference<>();
        version.set("");
        FabricLoader.getInstance().getModContainer(ID).ifPresent(container -> version.set(container.getMetadata().getVersion().getFriendlyString()));
        return version.get();
    }
}
