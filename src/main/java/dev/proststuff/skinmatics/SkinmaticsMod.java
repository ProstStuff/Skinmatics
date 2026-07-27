package dev.proststuff.skinmatics;

import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public interface SkinmaticsMod {
    String ID = "skinmatics";
    Logger LOGGER = LoggerFactory.getLogger("Skinmatics");

    static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    static SimpleIdentifier ofSimple(String path) {
        return SimpleIdentifier.of(ID, path);
    }

    static Version getVersion(String modId) {
        AtomicReference<Version> version = new AtomicReference<>();
        FabricLoader.getInstance().getModContainer(modId).ifPresent(container -> version.set(container.getMetadata().getVersion()));
        return version.get();
    }

    static Version getVersion() {
        return getVersion(ID);
    }

    static String getVersionString(String modId) {
        return getVersion(modId).getFriendlyString();
    }

    static String getVersionString() {
        return getVersionString(ID);
    }
}
