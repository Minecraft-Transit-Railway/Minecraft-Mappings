package org.mtr.mapping.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
	final List<Supplier<BlockEntityType<? extends BlockEntityExtension>>> BLOCK_ENTITY_TYPES = new ArrayList<>();
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
	public void registerBlockEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
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
