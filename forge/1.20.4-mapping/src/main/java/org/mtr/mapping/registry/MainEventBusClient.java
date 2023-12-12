package org.mtr.mapping.registry;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.mtr.mapping.holder.ClientWorld;

import java.util.function.Consumer;

public final class MainEventBusClient {

	static Runnable startClientTickRunnable = () -> {
	};
	static Runnable endClientTickRunnable = () -> {
	};
	static Runnable clientJoinRunnable = () -> {
	};
	static Runnable clientDisconnectRunnable = () -> {
	};
	static Consumer<ClientWorld> startWorldTickRunnable = world -> {
	};
	static Consumer<ClientWorld> endWorldTickRunnable = world -> {
	};

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		switch (event.phase) {
			case START -> startClientTickRunnable.run();
			case END -> endClientTickRunnable.run();
		}
	}

	@SubscribeEvent
	public static void worldTick(TickEvent.LevelTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.level instanceof ClientLevel) {
			switch (event.phase) {
				case START -> startWorldTickRunnable.accept(new ClientWorld((ClientLevel) event.level));
				case END -> endWorldTickRunnable.accept(new ClientWorld((ClientLevel) event.level));
			}
		}
	}

	@SubscribeEvent
	public static void clientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
		clientJoinRunnable.run();
	}

	@SubscribeEvent
	public static void clientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		clientDisconnectRunnable.run();
	}
}
