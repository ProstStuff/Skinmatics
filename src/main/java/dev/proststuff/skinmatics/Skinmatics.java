package dev.proststuff.skinmatics;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Skinmatics implements ModInitializer {
	public static final String ID = "skinmatics";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {}

	public static Identifier of(String path) {
		return Identifier.of(ID, path);
	}
}