package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
		ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public static void registerServerStarted(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public static void registerServerStopping(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public static void registerServerStopped(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public static void registerStartServerTick(Runnable runnable) {
		ServerTickEvents.START_SERVER_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public static void registerEndServerTick(Runnable runnable) {
		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public static void registerStartWorldTick(Consumer<ServerWorld> consumer) {
		ServerTickEvents.START_WORLD_TICK.register(serverWorld -> consumer.accept(new ServerWorld(serverWorld)));
	}

	@MappedMethod
	public static void registerEndWorldTick(Consumer<ServerWorld> consumer) {
		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> consumer.accept(new ServerWorld(serverWorld)));
	}

	@MappedMethod
	public static void registerPlayerJoin(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> consumer.accept(new MinecraftServer(server), new ServerPlayerEntity(handler.player)));
	}

	@MappedMethod
	public static void registerPlayerDisconnect(BiConsumer<MinecraftServer, ServerPlayerEntity> consumer) {
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> consumer.accept(new MinecraftServer(server), new ServerPlayerEntity(handler.player)));
	}

	@MappedMethod
	public static void registerChunkLoad(BiConsumer<ServerWorld, WorldChunk> consumer) {
		ServerChunkEvents.CHUNK_LOAD.register((serverWorld, worldChunk) -> consumer.accept(new ServerWorld(serverWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public static void registerChunkUnload(BiConsumer<ServerWorld, WorldChunk> consumer) {
		ServerChunkEvents.CHUNK_UNLOAD.register((serverWorld, worldChunk) -> consumer.accept(new ServerWorld(serverWorld), new WorldChunk(worldChunk)));
	}
}
