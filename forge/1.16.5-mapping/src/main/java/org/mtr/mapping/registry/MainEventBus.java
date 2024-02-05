package org.mtr.mapping.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
	static BiConsumer<MinecraftServer, ServerPlayerEntity> playerJoinRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	static BiConsumer<MinecraftServer, ServerPlayerEntity> playerDisconnectRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	static final List<Supplier<CommandBuilder<LiteralArgumentBuilder<CommandSource>>>> COMMANDS = new ArrayList<>();

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

	@SubscribeEvent
	public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		final PlayerEntity playerEntity = event.getPlayer();
		if (playerEntity instanceof net.minecraft.entity.player.ServerPlayerEntity) {
			final net.minecraft.entity.player.ServerPlayerEntity serverPlayerEntity = (net.minecraft.entity.player.ServerPlayerEntity) playerEntity;
			playerJoinRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		final PlayerEntity playerEntity = event.getPlayer();
		if (playerEntity instanceof net.minecraft.entity.player.ServerPlayerEntity) {
			final net.minecraft.entity.player.ServerPlayerEntity serverPlayerEntity = (net.minecraft.entity.player.ServerPlayerEntity) playerEntity;
			playerDisconnectRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		COMMANDS.forEach(supplier -> event.getDispatcher().register(supplier.get().argumentBuilder));
	}
}
