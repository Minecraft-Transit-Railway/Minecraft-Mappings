package org.mtr.mapping.tool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mtr.mapping.annotation.MappedMethod;

public abstract class DummyClass {

	private static boolean logging = false;
	private static final Logger LOGGER = Logger.getLogger("global");

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
			LOGGER.log(Level.INFO, e.getMessage(), e);
		}
	}
}
