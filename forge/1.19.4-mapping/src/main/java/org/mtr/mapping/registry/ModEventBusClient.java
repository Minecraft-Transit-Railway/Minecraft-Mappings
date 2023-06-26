package org.mtr.mapping.registry;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ModEventBusClient {

	static final List<Consumer<EntityRenderersEvent.RegisterRenderers>> OBJECTS_TO_REGISTER = new ArrayList<>();

	@SubscribeEvent
	public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		OBJECTS_TO_REGISTER.forEach(consumer -> consumer.accept(event));
	}
}
