package org.mtr.mapping.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.mtr.mapping.holder.SpriteProvider;
import org.mtr.mapping.mapper.ParticleFactoryExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ModEventBusClient {

	static Runnable resourceReloadRunnable = () -> {
	};
	static final List<Runnable> CLIENT_OBJECTS_TO_REGISTER = new ArrayList<>();
	static final List<Runnable> CLIENT_OBJECTS_TO_REGISTER_QUEUED = new ArrayList<>();
	static final List<Consumer<EntityRenderersEvent.RegisterRenderers>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
	static final List<Consumer<ColorHandlerEvent.Block>> BLOCK_COLORS = new ArrayList<>();
	static final List<Consumer<ColorHandlerEvent.Item>> ITEM_COLORS = new ArrayList<>();
	static final List<Tuple<ParticleTypeRegistryObject, Function<SpriteProvider, ParticleFactoryExtension>>> PARTICLE_FACTORIES = new ArrayList<>();

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
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BLOCK_COLORS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ITEM_COLORS.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		PARTICLE_FACTORIES.forEach(tuple -> Minecraft.getInstance().particleEngine.register(tuple.getA().get().data, spriteProvider -> tuple.getB().apply(new SpriteProvider(spriteProvider))));
	}

	@SubscribeEvent
	public static void resourceReload(TextureStitchEvent event) {
		if (event.getMap().location().getPath().endsWith("blocks.png")) {
			resourceReloadRunnable.run();
		}
	}
}
