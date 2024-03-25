package org.mtr.mapping.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.SoundEvent;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ModEventBus {

	static final Map<Identifier, Supplier<Block>> BLOCKS = new HashMap<>();
	static final Map<Identifier, Supplier<BlockItemExtension>> BLOCK_ITEMS = new HashMap<>();
	static final Map<Identifier, Supplier<Item>> ITEMS = new HashMap<>();
	static final Map<Identifier, Supplier<BlockEntityType<? extends BlockEntityExtension>>> BLOCK_ENTITY_TYPES = new HashMap<>();
	static final Map<Identifier, Supplier<EntityType<? extends EntityExtension>>> ENTITY_TYPES = new HashMap<>();
	static final Map<Identifier, Supplier<ParticleType<?>>> PARTICLE_TYPES = new HashMap<>();
	static final Map<Identifier, Supplier<SoundEvent>> SOUND_EVENTS = new HashMap<>();
	static final List<CreativeModeTabHolder> CREATIVE_MODE_TABS = new ArrayList<>();

	@SubscribeEvent
	public static void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BLOCKS, helper -> BLOCKS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get().data)));
		event.register(ForgeRegistries.Keys.ITEMS, helper -> {
			BLOCK_ITEMS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get()));
			ITEMS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get().data));
		});
		event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> BLOCK_ENTITY_TYPES.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> ENTITY_TYPES.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.PARTICLE_TYPES, helper -> PARTICLE_TYPES.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> SOUND_EVENTS.forEach(((identifier, supplier) -> helper.register(identifier.data, supplier.get().data))));
	}

	@SubscribeEvent
	public static void buildContents(CreativeModeTabEvent.Register event) {
		CREATIVE_MODE_TABS.forEach(creativeModeTabHolder -> event.registerCreativeModeTab(creativeModeTabHolder.identifier, builder -> builder
				.title(Component.translatable(String.format("itemGroup.%s.%s", creativeModeTabHolder.identifier.getNamespace(), creativeModeTabHolder.identifier.getPath())))
				.icon(() -> creativeModeTabHolder.iconSupplier.get().data)
				.displayItems((params, output) -> creativeModeTabHolder.itemSuppliers.forEach(itemSupplier -> output.accept(itemSupplier.get().data)))
				.build()
		));
	}
}
