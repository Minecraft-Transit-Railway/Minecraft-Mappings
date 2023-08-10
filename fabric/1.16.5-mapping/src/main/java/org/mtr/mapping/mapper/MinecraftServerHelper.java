package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Consumer;

public final class MinecraftServerHelper extends DummyClass {

	@MappedMethod
	public static void iterateWorlds(MinecraftServer minecraftServer, Consumer<ServerWorld> consumer) {
		minecraftServer.getWorlds().forEach(serverWorld -> consumer.accept(new ServerWorld(serverWorld)));
	}

	@MappedMethod
	public static Identifier getWorldId(World world) {
		return new Identifier(world.getRegistryKey().getValue());
	}
}
