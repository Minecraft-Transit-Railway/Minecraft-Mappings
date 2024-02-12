package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.WorldChunk;
import org.mtr.mapping.tool.DummyClass;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistryClient extends DummyClass {

	@MappedMethod
	public static void registerStartClientTick(Runnable runnable) {
		ClientTickEvents.START_CLIENT_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public static void registerEndClientTick(Runnable runnable) {
		ClientTickEvents.END_CLIENT_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public static void registerStartWorldTick(Consumer<ClientWorld> consumer) {
		ClientTickEvents.START_WORLD_TICK.register(clientWorld -> consumer.accept(new ClientWorld(clientWorld)));
	}

	@MappedMethod
	public static void registerEndWorldTick(Consumer<ClientWorld> consumer) {
		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> consumer.accept(new ClientWorld(clientWorld)));
	}

	@MappedMethod
	public static void registerClientJoin(Runnable runnable) {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> runnable.run());
	}

	@MappedMethod
	public static void registerClientDisconnect(Runnable runnable) {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> runnable.run());
	}

	@MappedMethod
	public static void registerChunkLoad(BiConsumer<ClientWorld, WorldChunk> consumer) {
		ClientChunkEvents.CHUNK_LOAD.register((clientWorld, worldChunk) -> consumer.accept(new ClientWorld(clientWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public static void registerChunkUnload(BiConsumer<ClientWorld, WorldChunk> consumer) {
		ClientChunkEvents.CHUNK_UNLOAD.register((clientWorld, worldChunk) -> consumer.accept(new ClientWorld(clientWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public static void registerResourceReloadEvent(Runnable runnable) {
		final Identifier identifier = new Identifier(Integer.toHexString(new Random().nextInt()), "resource");
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Deprecated
			@Override
			public final Identifier getFabricId() {
				return identifier;
			}

			@Deprecated
			@Override
			public final void reload(ResourceManager manager) {
				runnable.run();
			}
		});
	}
}
