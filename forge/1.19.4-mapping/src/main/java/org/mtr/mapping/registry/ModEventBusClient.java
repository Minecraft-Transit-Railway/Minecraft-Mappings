package org.mtr.mapping.registry;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ModEventBusClient {

	static final List<Runnable> CLIENT_OBJECTS_TO_REGISTER = new ArrayList<>();
	static final List<Runnable> CLIENT_OBJECTS_TO_REGISTER_QUEUED = new ArrayList<>();
	static final List<Consumer<EntityRenderersEvent.RegisterRenderers>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
	static final List<Consumer<RegisterKeyMappingsEvent>> KEY_MAPPINGS = new ArrayList<>();
	static final List<Consumer<RegisterColorHandlersEvent.Block>> BLOCK_COLORS = new ArrayList<>();
	static final List<Consumer<RegisterColorHandlersEvent.Item>> ITEM_COLORS = new ArrayList<>();

	@SubscribeEvent
	public static void registerClient(FMLClientSetupEvent event) {
		CLIENT_OBJECTS_TO_REGISTER.forEach(Runnable::run);
		event.enqueueWork(() -> CLIENT_OBJECTS_TO_REGISTER_QUEUED.forEach(Runnable::run));
	}

	@SubscribeEvent
	public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		BLOCK_ENTITY_RENDERERS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		KEY_MAPPINGS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		BLOCK_COLORS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		ITEM_COLORS.forEach(consumer -> consumer.accept(event));
	}
}
