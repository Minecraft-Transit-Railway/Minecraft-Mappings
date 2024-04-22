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
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MainEventBus {

	Consumer<MinecraftServer> serverStartingConsumer = minecraftServer -> {
	};
	Consumer<MinecraftServer> serverStartedConsumer = minecraftServer -> {
	};
	Consumer<MinecraftServer> serverStoppingConsumer = minecraftServer -> {
	};
	Consumer<MinecraftServer> serverStoppedConsumer = minecraftServer -> {
	};
	Runnable startServerTickRunnable = () -> {
	};
	Runnable endServerTickRunnable = () -> {
	};
	Consumer<ServerWorld> startWorldTickRunnable = world -> {
	};
	Consumer<ServerWorld> endWorldTickRunnable = world -> {
	};
	BiConsumer<MinecraftServer, ServerPlayerEntity> playerJoinRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	BiConsumer<MinecraftServer, ServerPlayerEntity> playerDisconnectRunnable = (minecraftServer, serverPlayerEntity) -> {
	};
	BiConsumer<ServerWorld, WorldChunk> chunkLoadConsumer = (world, chunk) -> {
	};
	BiConsumer<ServerWorld, WorldChunk> chunkUnloadConsumer = (world, chunk) -> {
	};
	final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS = new ArrayList<>();

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		serverStartingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartedEvent event) {
		serverStartedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public void serverStopping(FMLServerStoppingEvent event) {
		serverStoppingConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public void serverStopped(FMLServerStoppedEvent event) {
		serverStoppedConsumer.accept(new MinecraftServer(event.getServer()));
	}

	@SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent event) {
		switch (event.phase) {
			case START -> startServerTickRunnable.run();
			case END -> endServerTickRunnable.run();
		}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.side == LogicalSide.SERVER && event.world instanceof ServerLevel) {
			switch (event.phase) {
				case START -> startWorldTickRunnable.accept(new ServerWorld((ServerLevel) event.world));
				case END -> endWorldTickRunnable.accept(new ServerWorld((ServerLevel) event.world));
			}
		}
	}

	@SubscribeEvent
	public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		final Player playerEntity = event.getPlayer();
		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
			playerJoinRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		final Player playerEntity = event.getPlayer();
		if (playerEntity instanceof ServerPlayer serverPlayerEntity) {
			playerDisconnectRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		if (event.getWorld() instanceof ServerLevel && event.getChunk() instanceof LevelChunk) {
			chunkLoadConsumer.accept(new ServerWorld((ServerLevel) event.getWorld()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Load event) {
		if (event.getWorld() instanceof ServerLevel && event.getChunk() instanceof LevelChunk) {
			chunkUnloadConsumer.accept(new ServerWorld((ServerLevel) event.getWorld()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		COMMANDS.forEach(consumer -> consumer.accept(event.getDispatcher()));
	}
}
