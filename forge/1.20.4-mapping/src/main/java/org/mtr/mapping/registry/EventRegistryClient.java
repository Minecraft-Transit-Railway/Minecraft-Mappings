package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.WorldChunk;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistryClient extends DummyClass {

	private final MainEventBusClient mainEventBusClient;
	private final ModEventBusClient modEventBusClient;

	@Deprecated
	EventRegistryClient(MainEventBusClient mainEventBusClient, ModEventBusClient modEventBusClient) {
		this.mainEventBusClient = mainEventBusClient;
		this.modEventBusClient = modEventBusClient;
	}

	@MappedMethod
	public void registerStartClientTick(Runnable runnable) {
		mainEventBusClient.startClientTickRunnable = runnable;
	}

	@MappedMethod
	public void registerEndClientTick(Runnable runnable) {
		mainEventBusClient.endClientTickRunnable = runnable;
	}

	@MappedMethod
	public void registerStartWorldTick(Consumer<ClientWorld> consumer) {
		mainEventBusClient.startWorldTickRunnable = consumer;
	}

	@MappedMethod
	public void registerEndWorldTick(Consumer<ClientWorld> consumer) {
		mainEventBusClient.endWorldTickRunnable = consumer;
	}

	@MappedMethod
	public void registerClientJoin(Runnable runnable) {
		mainEventBusClient.clientJoinRunnable = runnable;
	}

	@MappedMethod
	public void registerClientDisconnect(Runnable runnable) {
		mainEventBusClient.clientDisconnectRunnable = runnable;
	}

	@MappedMethod
	public void registerChunkLoad(BiConsumer<ClientWorld, WorldChunk> consumer) {
		mainEventBusClient.chunkLoadConsumer = consumer;
	}

	@MappedMethod
	public void registerChunkUnload(BiConsumer<ClientWorld, WorldChunk> consumer) {
		mainEventBusClient.chunkUnloadConsumer = consumer;
	}

	@MappedMethod
	public void registerResourceReloadEvent(Runnable runnable) {
		modEventBusClient.resourceReloadRunnable = runnable;
	}
}
