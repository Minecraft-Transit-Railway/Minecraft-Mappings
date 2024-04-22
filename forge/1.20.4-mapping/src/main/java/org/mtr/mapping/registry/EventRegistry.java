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

	private final MainEventBus mainEventBus;

	@Deprecated
	EventRegistry(MainEventBus mainEventBus) {
		this.mainEventBus = mainEventBus;
	}

	@MappedMethod
	public void registerServerStarting(Consumer<MinecraftServer> consumer) {
		mainEventBus.serverStartingConsumer = consumer;
	}

	@MappedMethod
	public void registerServerStarted(Consumer<MinecraftServer> consumer) {
		mainEventBus.serverStartedConsumer = consumer;
	}

	@MappedMethod
	public void registerServerStopping(Consumer<MinecraftServer> consumer) {
		mainEventBus.serverStoppingConsumer = consumer;
	}

	@MappedMethod
	public void registerServerStopped(Consumer<MinecraftServer> consumer) {
		mainEventBus.serverStoppedConsumer = consumer;
	}

	@MappedMethod
	public void registerStartServerTick(Runnable runnable) {
		mainEventBus.startServerTickRunnable = runnable;
	}

	@MappedMethod
	public void registerEndServerTick(Runnable runnable) {
		mainEventBus.endServerTickRunnable = runnable;
	}

	@MappedMethod
	public void registerStartWorldTick(Consumer<ServerWorld> consumer) {
		mainEventBus.startWorldTickRunnable = consumer;
	}

	@MappedMethod
	public void registerEndWorldTick(Consumer<ServerWorld> consumer) {
		mainEventBus.endWorldTickRunnable = consumer;
	}

	@MappedMethod
	public void registerPlayerJoin(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		mainEventBus.playerJoinRunnable = consumer;
	}

	@MappedMethod
	public void registerPlayerDisconnect(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		mainEventBus.playerDisconnectRunnable = consumer;
	}

	@MappedMethod
	public void registerChunkLoad(BiConsumer<ServerWorld, WorldChunk> consumer) {
		mainEventBus.chunkLoadConsumer = consumer;
	}

	@MappedMethod
	public void registerChunkUnload(BiConsumer<ServerWorld, WorldChunk> consumer) {
		mainEventBus.chunkUnloadConsumer = consumer;
	}
}
