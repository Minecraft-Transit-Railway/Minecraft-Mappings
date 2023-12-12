package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Consumer;

public class EventRegistryClient extends DummyClass {

	@MappedMethod
	public static void registerStartClientTick(Runnable runnable) {
		MainEventBusClient.startClientTickRunnable = runnable;
	}

	@MappedMethod
	public static void registerEndClientTick(Runnable runnable) {
		MainEventBusClient.endClientTickRunnable = runnable;
	}

	@MappedMethod
	public static void registerStartWorldTick(Consumer<ClientWorld> consumer) {
		MainEventBusClient.startWorldTickRunnable = consumer;
	}

	@MappedMethod
	public static void registerEndWorldTick(Consumer<ClientWorld> consumer) {
		MainEventBusClient.endWorldTickRunnable = consumer;
	}

	@MappedMethod
	public static void registerClientJoin(Runnable runnable) {
		MainEventBusClient.clientJoinRunnable = runnable;
	}

	@MappedMethod
	public static void registerClientDisconnect(Runnable runnable) {
		MainEventBusClient.clientDisconnectRunnable = runnable;
	}
}
