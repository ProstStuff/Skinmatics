package com.fraynn.skinmatics;

import com.fraynn.skinmatics.avatar.feature.runtime.AvatarRuntimeTypes;
import com.fraynn.skinmatics.avatar.feature.runtime.light.DynamicLights;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.compat.iris.IIrisCompatHelper;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import com.fraynn.utilitary.platform.ModInfo;
import com.fraynn.utilitary.platform.services.IPlatformHelper;
import com.fraynn.utilitary.util.SimpleIdentifier;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Skinmatics {
	public static final String ID = "skinmatics";
	public static final String NAME = "Skinmatics";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static ModInfo INFO = null;

	public static Identifier of(String name) {
		return Identifier.fromNamespaceAndPath(ID, name);
	}

	public static SimpleIdentifier ofSimple(String name) {
		return SimpleIdentifier.of(ID, name);
	}

	public static void init() {
		AvatarFeatureTypes.init();
		DynamicLights.init();
		AvatarRuntimeTypes.init();
		SkinmaticsKeyMappings.init();

		if (IPlatformHelper.INSTANCE.isModLoaded("iris")) {
			LOGGER.info("Initializing Skinmatics Compat: Iris...");
			IIrisCompatHelper.INSTANCE.ifSuccessful(IIrisCompatHelper::registerPBRLoader);
		}

		if (IPlatformHelper.INSTANCE.isModLoaded("lambdynlights")) {
			LOGGER.info("Initializing Skinmatics Compat: LambDynamicLights...");
			// Empty because they're loaded automatically
		}

		SkinmaticsConfig.load();
		INFO = IPlatformHelper.INSTANCE.getModInfo(ID);
	}
}