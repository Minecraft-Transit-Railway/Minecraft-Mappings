package org.mtr.mapping.registry;

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
			case START:
				startClientTickRunnable.run();
				break;
			case END:
				endClientTickRunnable.run();
				break;
		}
	}

	@SubscribeEvent
	public static void worldTick(TickEvent.WorldTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.world instanceof net.minecraft.client.world.ClientWorld) {
			switch (event.phase) {
				case START:
					startWorldTickRunnable.accept(new ClientWorld((net.minecraft.client.world.ClientWorld) event.world));
					break;
				case END:
					endWorldTickRunnable.accept(new ClientWorld((net.minecraft.client.world.ClientWorld) event.world));
					break;
			}
		}
	}

	@SubscribeEvent
	public static void clientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
		clientJoinRunnable.run();
	}

	@SubscribeEvent
	public static void clientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		clientDisconnectRunnable.run();
	}
}
