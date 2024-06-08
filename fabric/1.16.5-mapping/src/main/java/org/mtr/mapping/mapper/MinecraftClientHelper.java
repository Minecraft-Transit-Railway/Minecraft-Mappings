package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.tool.DummyClass;

import java.io.File;
import java.util.function.Consumer;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return MinecraftClient.getInstance().options.viewDistance;
	}

	@MappedMethod
	public static File getResourcePackDirectory() {
		return MinecraftClient.getInstance().getResourcePackDir();
	}

	@MappedMethod
	public static void getEntities(Consumer<Entity> consumer) {
		final ClientWorld clientWorld = MinecraftClient.getInstance().world;
		if (clientWorld != null) {
			clientWorld.getEntities().forEach(entity -> consumer.accept(new Entity(entity)));
		}
	}

	@MappedMethod
	public static void addEntity(EntityAbstractMapping entity) {
		final ClientWorld clientWorld = MinecraftClient.getInstance().world;
		if (clientWorld != null) {
			clientWorld.addEntity(entity.getEntityId(), entity);
		}
	}
}
