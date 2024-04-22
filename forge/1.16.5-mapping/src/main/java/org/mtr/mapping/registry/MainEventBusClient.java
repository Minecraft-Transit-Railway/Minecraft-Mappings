package org.mtr.mapping.registry;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
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
			case START:
				startClientTickRunnable.run();
				break;
			case END:
				endClientTickRunnable.run();
				break;
		}
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
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
	public void clientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
		clientJoinRunnable.run();
	}

	@SubscribeEvent
	public void clientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		clientDisconnectRunnable.run();
	}

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		if (event.getWorld() instanceof net.minecraft.client.world.ClientWorld && event.getChunk() instanceof Chunk) {
			chunkLoadConsumer.accept(new ClientWorld((net.minecraft.client.world.ClientWorld) event.getWorld()), new WorldChunk((Chunk) event.getChunk()));
		}
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Load event) {
		if (event.getWorld() instanceof net.minecraft.client.world.ClientWorld && event.getChunk() instanceof Chunk) {
			chunkUnloadConsumer.accept(new ClientWorld((net.minecraft.client.world.ClientWorld) event.getWorld()), new WorldChunk((Chunk) event.getChunk()));
		}
	}
}
