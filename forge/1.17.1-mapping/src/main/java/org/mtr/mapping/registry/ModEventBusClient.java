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

	Runnable resourceReloadRunnable = () -> {
	};
	final List<Runnable> clientObjectsToRegister = new ArrayList<>();
	final List<Runnable> clientObjectsToRegisterQueued = new ArrayList<>();
	final List<Consumer<EntityRenderersEvent.RegisterRenderers>> blockEntityRenderers = new ArrayList<>();
	final List<Consumer<ColorHandlerEvent.Block>> blockColors = new ArrayList<>();
	final List<Consumer<ColorHandlerEvent.Item>> itemColors = new ArrayList<>();
	final List<Tuple<ParticleTypeRegistryObject, Function<SpriteProvider, ParticleFactoryExtension>>> particleFactories = new ArrayList<>();

	@SubscribeEvent
	public void registerClient(FMLClientSetupEvent event) {
		clientObjectsToRegister.forEach(Runnable::run);
		event.enqueueWork(() -> clientObjectsToRegisterQueued.forEach(Runnable::run));
	}

	@SubscribeEvent
	public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		blockEntityRenderers.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		blockColors.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		itemColors.forEach(consumer -> consumer.accept(event));
	}

	@SubscribeEvent
	public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		particleFactories.forEach(tuple -> Minecraft.getInstance().particleEngine.register(tuple.getA().get().data, spriteProvider -> tuple.getB().apply(new SpriteProvider(spriteProvider))));
	}

	@SubscribeEvent
	public void resourceReload(TextureStitchEvent.Post event) {
		if (event.getMap().location().getPath().endsWith("blocks.png")) {
			resourceReloadRunnable.run();
		}
	}
}
