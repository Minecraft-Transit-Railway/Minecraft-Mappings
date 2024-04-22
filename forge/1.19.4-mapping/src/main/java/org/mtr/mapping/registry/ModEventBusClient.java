package org.mtr.mapping.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.mtr.mapping.holder.SpriteProvider;
import org.mtr.mapping.mapper.ParticleFactoryExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModEventBusClient {

	Runnable resourceReloadRunnable = () -> {
	};
	final List<Runnable> CLIENT_OBJECTS_TO_REGISTER = new ArrayList<>();
	final List<Runnable> CLIENT_OBJECTS_TO_REGISTER_QUEUED = new ArrayList<>();
	final List<Consumer<EntityRenderersEvent.RegisterRenderers>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
	final List<Consumer<RegisterKeyMappingsEvent>> KEY_MAPPINGS = new ArrayList<>();
	final List<Consumer<RegisterColorHandlersEvent.Block>> BLOCK_COLORS = new ArrayList<>();
	final List<Consumer<RegisterColorHandlersEvent.Item>> ITEM_COLORS = new ArrayList<>();
	final List<Tuple<ParticleTypeRegistryObject, Function<SpriteProvider, ParticleFactoryExtension>>> PARTICLE_FACTORIES = new ArrayList<>();

	@SubscribeEvent
	public void registerClient(FMLClientSetupEvent event) {
		CLIENT_OBJECTS_TO_REGISTER.forEach(Runnable::run);
		event.enqueueWork(() -> CLIENT_OBJECTS_TO_REGISTER_QUEUED.forEach(Runnable::run));
	}

	@SubscribeEvent
	public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		BLOCK_ENTITY_RENDERERS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerKeyMappings(RegisterKeyMappingsEvent event) {
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

	@SubscribeEvent
	public void registerParticleFactories(RegisterParticleProvidersEvent event) {
		PARTICLE_FACTORIES.forEach(tuple -> Minecraft.getInstance().particleEngine.register(tuple.getA().get().data, spriteProvider -> tuple.getB().apply(new SpriteProvider(spriteProvider))));
	}

	@SubscribeEvent
	public void resourceReload(TextureStitchEvent event) {
		if (event.getAtlas().location().getPath().endsWith("blocks.png")) {
			resourceReloadRunnable.run();
		}
	}
}
