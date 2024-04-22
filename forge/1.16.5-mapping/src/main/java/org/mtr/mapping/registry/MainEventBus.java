package org.mtr.mapping.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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
	final List<Consumer<CommandDispatcher<CommandSource>>> commands = new ArrayList<>();

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
			case START:
				startServerTickRunnable.run();
				break;
			case END:
				endServerTickRunnable.run();
				break;
		}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
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
	public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		final PlayerEntity playerEntity = event.getPlayer();
		if (playerEntity instanceof net.minecraft.entity.player.ServerPlayerEntity) {
			final net.minecraft.entity.player.ServerPlayerEntity serverPlayerEntity = (net.minecraft.entity.player.ServerPlayerEntity) playerEntity;
			playerJoinRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
		final PlayerEntity playerEntity = event.getPlayer();
		if (playerEntity instanceof net.minecraft.entity.player.ServerPlayerEntity) {
			final net.minecraft.entity.player.ServerPlayerEntity serverPlayerEntity = (net.minecraft.entity.player.ServerPlayerEntity) playerEntity;
			playerDisconnectRunnable.accept(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
		}
	}

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		if (event.getWorld() instanceof net.minecraft.world.server.ServerWorld && event.getChunk() instanceof Chunk) {
			chunkLoadConsumer.accept(new ServerWorld((net.minecraft.world.server.ServerWorld) event.getWorld()), new WorldChunk((Chunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Load event) {
		if (event.getWorld() instanceof net.minecraft.world.server.ServerWorld && event.getChunk() instanceof Chunk) {
			chunkUnloadConsumer.accept(new ServerWorld((net.minecraft.world.server.ServerWorld) event.getWorld()), new WorldChunk((Chunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		commands.forEach(consumer -> consumer.accept(event.getDispatcher()));
	}
}
