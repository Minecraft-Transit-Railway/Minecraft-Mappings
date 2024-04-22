package org.mtr.mapping.registry;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.WorldChunk;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class MainEventBusClient {

	Runnable startClientTickRunnable = () -> {
	};
	Runnable endClientTickRunnable = () -> {
	};
	Runnable clientJoinRunnable = () -> {
	};
	Runnable clientDisconnectRunnable = () -> {
	};
	Consumer<ClientWorld> startWorldTickRunnable = world -> {
	};
	Consumer<ClientWorld> endWorldTickRunnable = world -> {
	};
	BiConsumer<ClientWorld, WorldChunk> chunkLoadConsumer = (world, chunk) -> {
	};
	BiConsumer<ClientWorld, WorldChunk> chunkUnloadConsumer = (world, chunk) -> {
	};

	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event) {
		switch (event.phase) {
			case START -> startClientTickRunnable.run();
			case END -> endClientTickRunnable.run();
		}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.LevelTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.level instanceof ClientLevel) {
			switch (event.phase) {
				case START -> startWorldTickRunnable.accept(new ClientWorld((ClientLevel) event.level));
				case END -> endWorldTickRunnable.accept(new ClientWorld((ClientLevel) event.level));
			}
		}
	}

	@SubscribeEvent
	public void clientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
		clientJoinRunnable.run();
	}

	@SubscribeEvent
	public void clientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		clientDisconnectRunnable.run();
	}

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ClientLevel && event.getChunk() instanceof LevelChunk) {
			chunkLoadConsumer.accept(new ClientWorld((ClientLevel) event.getLevel()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Load event) {
		if (event.getLevel() instanceof ClientLevel && event.getChunk() instanceof LevelChunk) {
			chunkUnloadConsumer.accept(new ClientWorld((ClientLevel) event.getLevel()), new WorldChunk((LevelChunk) event.getChunk()));
		}
	}
}
