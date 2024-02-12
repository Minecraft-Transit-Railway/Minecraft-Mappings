package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.WorldChunk;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistry extends DummyClass {

	@MappedMethod
	public static void registerServerStarting(Consumer<MinecraftServer> consumer) {
		MainEventBus.serverStartingConsumer = consumer;
	}

	@MappedMethod
	public static void registerServerStarted(Consumer<MinecraftServer> consumer) {
		MainEventBus.serverStartedConsumer = consumer;
	}

	@MappedMethod
	public static void registerServerStopping(Consumer<MinecraftServer> consumer) {
		MainEventBus.serverStoppingConsumer = consumer;
	}

	@MappedMethod
	public static void registerServerStopped(Consumer<MinecraftServer> consumer) {
		MainEventBus.serverStoppedConsumer = consumer;
	}

	@MappedMethod
	public static void registerStartServerTick(Runnable runnable) {
		MainEventBus.startServerTickRunnable = runnable;
	}

	@MappedMethod
	public static void registerEndServerTick(Runnable runnable) {
		MainEventBus.endServerTickRunnable = runnable;
	}

	@MappedMethod
	public static void registerStartWorldTick(Consumer<ServerWorld> consumer) {
		MainEventBus.startWorldTickRunnable = consumer;
	}

	@MappedMethod
	public static void registerEndWorldTick(Consumer<ServerWorld> consumer) {
		MainEventBus.endWorldTickRunnable = consumer;
	}

	@MappedMethod
	public static void registerPlayerJoin(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		MainEventBus.playerJoinRunnable = consumer;
	}

	@MappedMethod
	public static void registerPlayerDisconnect(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		MainEventBus.playerDisconnectRunnable = consumer;
	}

	@MappedMethod
	public static void registerChunkLoad(BiConsumer<ServerWorld, WorldChunk> consumer) {
		MainEventBus.chunkLoadConsumer = consumer;
	}

	@MappedMethod
	public static void registerChunkUnload(BiConsumer<ServerWorld, WorldChunk> consumer) {
		MainEventBus.chunkUnloadConsumer = consumer;
	}
}
