package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.tool.DummyClass;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EntityHelper extends DummyClass {

	public static final List<UUID> HIDDEN_PLAYERS = new ArrayList<>();

	@MappedMethod
	public static float getPitch(Entity entity) {
		return entity.data.getPitch();
	}

	@MappedMethod
	public static float getYaw(Entity entity) {
		return entity.data.getYaw();
	}

	@MappedMethod
	public static void setPitch(Entity entity, float pitch) {
		entity.data.setPitch(pitch);
	}

	@MappedMethod
	public static void setYaw(Entity entity, float yaw) {
		entity.data.setYaw(yaw);
	}
}
