package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

import java.io.File;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return MinecraftClient.getInstance().options.viewDistance;
	}

	@MappedMethod
	public static File getResourcePackDirectory() {
		return MinecraftClient.getInstance().getResourcePackDir();
	}
}
