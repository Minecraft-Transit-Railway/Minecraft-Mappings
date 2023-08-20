package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public int getRenderDistance() {
		return MinecraftClient.getInstance().options.viewDistance;
	}
}
