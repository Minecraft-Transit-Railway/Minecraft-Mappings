package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return Minecraft.getInstance().options.renderDistance;
	}
}
