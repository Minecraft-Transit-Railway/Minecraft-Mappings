package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.tool.DummyClass;

public final class EntityHelper extends DummyClass {

	@MappedMethod
	public static float getPitch(Entity entity) {
		return entity.getPitch();
	}

	@MappedMethod
	public static float getYaw(Entity entity) {
		return entity.getYaw();
	}

	@MappedMethod
	public static void setPitch(Entity entity, float pitch) {
		entity.setPitch(pitch);
	}

	@MappedMethod
	public static void setYaw(Entity entity, float yaw) {
		entity.setYaw(yaw);
	}
}
