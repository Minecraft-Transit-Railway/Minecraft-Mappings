package org.mtr.mapping.registry;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ModEventBusClient {

	static final List<Runnable> CLIENT_OBJECTS_TO_REGISTER = new ArrayList<>();
	static final List<Consumer<ColorHandlerEvent.Block>> BLOCK_COLORS = new ArrayList<>();
	static final List<Consumer<ColorHandlerEvent.Item>> ITEM_COLORS = new ArrayList<>();

	@SubscribeEvent
	public static void registerClient(FMLCommonSetupEvent event) {
		CLIENT_OBJECTS_TO_REGISTER.forEach(Runnable::run);
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BLOCK_COLORS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ITEM_COLORS.forEach(consumer -> consumer.accept(event));
	}
}
