package dev.proststuff.skinmatics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class Skinmatics implements ModInitializer {
	public static final String ID = "skinmatics";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {}

	public static Identifier of(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

	public static String getVersion() {

		AtomicReference<String> version = new AtomicReference<>();
		version.set("<ERROR>");
		FabricLoader.getInstance().getModContainer(ID).ifPresent(container -> version.set(container.getMetadata().getVersion().getFriendlyString()));
		return version.get();
	}
}