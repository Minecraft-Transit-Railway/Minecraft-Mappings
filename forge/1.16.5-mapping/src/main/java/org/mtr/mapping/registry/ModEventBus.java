package org.mtr.mapping.registry;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModEventBus {

	final List<Supplier<Block>> BLOCKS = new ArrayList<>();
	final List<Supplier<BlockItemExtension>> BLOCK_ITEMS = new ArrayList<>();
	final List<Supplier<Item>> ITEMS = new ArrayList<>();
	final List<Supplier<TileEntityType<? extends BlockEntityExtension>>> BLOCK_ENTITY_TYPES = new ArrayList<>();
	final List<Supplier<EntityType<? extends EntityExtension>>> ENTITY_TYPES = new ArrayList<>();
	final List<Supplier<ParticleType<?>>> PARTICLE_TYPES = new ArrayList<>();
	final List<Supplier<SoundEvent>> SOUND_EVENTS = new ArrayList<>();

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		BLOCKS.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		BLOCK_ITEMS.forEach(supplier -> event.getRegistry().register(supplier.get()));
		ITEMS.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerBlockEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		BLOCK_ENTITY_TYPES.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
		ENTITY_TYPES.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
		PARTICLE_TYPES.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
		SOUND_EVENTS.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}
}
