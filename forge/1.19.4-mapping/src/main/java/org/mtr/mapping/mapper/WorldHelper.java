package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyClass;

public final class WorldHelper extends DummyClass {

	@MappedMethod
	public static long getTimeOfDay(ServerWorld serverWorld) {
		return serverWorld.data.getDayTime();
	}

	@MappedMethod
	public static long getTimeOfDay(ClientWorld clientWorld) {
		return clientWorld.data.getDayTime();
	}

	@MappedMethod
	public static long getTimeOfDay(World world) {
		return world.data.getDayTime();
	}
}
