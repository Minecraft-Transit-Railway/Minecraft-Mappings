package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.tool.DummyClass;

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
}
