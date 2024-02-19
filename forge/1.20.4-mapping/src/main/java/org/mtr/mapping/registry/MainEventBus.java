package org.mtr.mapping.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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
	static BiConsumer<MinecraftServer, ServerPlayerEntity> playerJoinRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	static BiConsumer<MinecraftServer, ServerPlayerEntity> playerDisconnectRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	static BiConsumer<ServerWorld, WorldChunk> chunkLoadConsumer = (world, chunk) -> {
	};
	static BiConsumer<ServerWorld, WorldChunk> chunkUnloadConsumer = (world, chunk) -> {
	};
	static final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS = new ArrayList<>();

	@SubscribeEvent
	public static void serverStarting(ServerStartingEvent event) {
		serverStartingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStarted(ServerStartedEvent event) {
		serverStartedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopping(ServerStoppingEvent event) {
		serverStoppingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverStopped(ServerStoppedEvent event) {
		serverStoppedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		switch (event.phase) {
			case START -> startServerTickRunnable.run();
			case END -> endServerTickRunnable.run();
		}
	}

	@SubscribeEvent
	public static void worldTick(TickEvent.LevelTickEvent event) {
		if (event.side == LogicalSide.SERVER && event.level instanceof ServerLevel) {
			switch (event.phase) {
				case START -> startWorldTickRunnable.accept(new ServerWorld((ServerLevel) event.level));
				case END -> endWorldTickRunnable.accept(new ServerWorld((ServerLevel) event.level));
			}
		}
	}

	@SubscribeEvent
	public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		final Player playerEntity = event.getEntity();
		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
			playerJoinRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		final Player playerEntity = event.getEntity();
		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
			playerDisconnectRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public static void chunkLoad(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel && event.getChunk() instanceof LevelChunk) {
			chunkLoadConsumer.accept(new ServerWorld((ServerLevel) event.getLevel()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public static void chunkUnload(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel && event.getChunk() instanceof LevelChunk) {
			chunkUnloadConsumer.accept(new ServerWorld((ServerLevel) event.getLevel()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		COMMANDS.forEach(consumer -> consumer.accept(event.getDispatcher()));
	}
}
