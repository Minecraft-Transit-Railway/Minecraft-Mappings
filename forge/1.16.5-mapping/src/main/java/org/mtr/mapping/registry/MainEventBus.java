package org.mtr.mapping.registry;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerWorld;

import java.util.function.Consumer;

public final class MainEventBus {

	static Consumer<MinecraftServer> serverStartingConsumer = minecraftServer -> {
	};
	static Consumer<MinecraftServer> serverStartedConsumer = minecraftServer -> {
	};
	static Consumer<MinecraftServer> serverStoppingConsumer = minecraftServer -> {
	};
	static Consumer<MinecraftServer> serverStoppedConsumer = minecraftServer -> {
	};
	static Runnable startServerTickRunnable = () -> {
	};
	static Runnable endServerTickRunnable = () -> {
	};
	static Consumer<ServerWorld> startWorldTickRunnable = world -> {
	};
	static Consumer<ServerWorld> endWorldTickRunnable = world -> {
	};

	@SubscribeEvent
	public static void serverStarting(FMLServerStartingEvent event) {
		serverStartingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStarted(FMLServerStartedEvent event) {
		serverStartedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopping(FMLServerStoppingEvent event) {
		serverStoppingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopped(FMLServerStoppedEvent event) {
		serverStoppedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		switch (event.phase) {
			case START:
				startServerTickRunnable.run();
				break;
			case END:
				endServerTickRunnable.run();
				break;
		}
	}

	@SubscribeEvent
	public static void worldTick(TickEvent.WorldTickEvent event) {
		if (event.side == LogicalSide.SERVER && event.world instanceof net.minecraft.world.server.ServerWorld) {
			switch (event.phase) {
				case START:
					startWorldTickRunnable.accept(new ServerWorld((net.minecraft.world.server.ServerWorld) event.world));
					break;
				case END:
					endWorldTickRunnable.accept(new ServerWorld((net.minecraft.world.server.ServerWorld) event.world));
					break;
			}
		}
	}
}
