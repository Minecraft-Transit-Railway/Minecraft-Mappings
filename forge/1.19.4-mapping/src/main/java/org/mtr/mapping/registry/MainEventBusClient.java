package org.mtr.mapping.registry;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.WorldRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;

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
	static EventRegistryClient.RenderWorldCallback renderWorldLastConsumer = (graphicsHolder, projectionMatrix, worldRenderer, tickDelta) -> {
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

	@SubscribeEvent
	public static void renderWorldLast(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			GraphicsHolder.createInstanceSafe(event.getPoseStack(), null, graphicsHolder -> renderWorldLastConsumer.accept(graphicsHolder, new Matrix4f(event.getProjectionMatrix()), new WorldRenderer(event.getLevelRenderer()), event.getPartialTick()));
		}
	}
}
