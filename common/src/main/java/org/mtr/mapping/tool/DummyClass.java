package org.mtr.mapping.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class DummyClass {

	private static boolean logging = false;
	private static final Logger LOGGER = LogManager.getLogger("MinecraftMappings");

	@MappedMethod
	public static void enableLogging() {
		logging = true;
		LOGGER.info("Minecraft Mappings logging enabled");
	}

	@MappedMethod
	public static void disableLogging() {
		logging = false;
		LOGGER.info("Minecraft Mappings logging disabled");
	}

	@MappedMethod
	public static void logInfo(String string) {
		if (logging) {
			LOGGER.info(string);
		}
	}

	@MappedMethod
	public static void logException(Exception e) {
		if (logging) {
			LOGGER.error(e);
		}
	}
}
