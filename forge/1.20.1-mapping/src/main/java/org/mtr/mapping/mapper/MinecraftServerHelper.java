package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MinecraftServerHelper extends DummyClass {

	@MappedMethod
	public static void iterateWorlds(MinecraftServer minecraftServer, Consumer<ServerWorld> consumer) {
		minecraftServer.data.getAllLevels().forEach(serverWorld -> consumer.accept(new ServerWorld(serverWorld)));
	}

	@MappedMethod
	public static void iteratePlayers(MinecraftServer minecraftServer, Consumer<ServerPlayerEntity> consumer) {
		minecraftServer.data.getPlayerList().getPlayers().forEach(serverPlayerEntity -> consumer.accept(new ServerPlayerEntity(serverPlayerEntity)));
	}

	@MappedMethod
	public static void iteratePlayers(ServerWorld serverWorld, Consumer<ServerPlayerEntity> consumer) {
		serverWorld.data.players().forEach(serverPlayerEntity -> consumer.accept(new ServerPlayerEntity(serverPlayerEntity)));
	}

	@MappedMethod
	public static void iteratePlayers(ServerWorld serverWorld, Predicate<ServerPlayerEntity> predicate, Consumer<ServerPlayerEntity> consumer) {
		serverWorld.data.getPlayers(serverPlayerEntity -> predicate.test(new ServerPlayerEntity(serverPlayerEntity))).forEach(serverPlayerEntity -> consumer.accept(new ServerPlayerEntity(serverPlayerEntity)));
	}

	@MappedMethod
	public static Identifier getWorldId(World world) {
		return new Identifier(world.data.dimension().location());
	}
}
