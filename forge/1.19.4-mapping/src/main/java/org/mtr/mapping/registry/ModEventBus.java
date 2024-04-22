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

	final Map<Identifier, Supplier<Block>> BLOCKS = new HashMap<>();
	final Map<Identifier, Supplier<BlockItemExtension>> BLOCK_ITEMS = new HashMap<>();
	final Map<Identifier, Supplier<Item>> ITEMS = new HashMap<>();
	final Map<Identifier, Supplier<BlockEntityType<? extends BlockEntityExtension>>> blockEntityTypes = new HashMap<>();
	final Map<Identifier, Supplier<EntityType<? extends EntityExtension>>> entityTypes = new HashMap<>();
	final Map<Identifier, Supplier<ParticleType<?>>> particleTypes = new HashMap<>();
	final Map<Identifier, Supplier<SoundEvent>> soundEvents = new HashMap<>();
	final List<CreativeModeTabHolder> creativeModeTabs = new ArrayList<>();

	@SubscribeEvent
	public void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BLOCKS, helper -> BLOCKS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get().data)));
		event.register(ForgeRegistries.Keys.ITEMS, helper -> {
			BLOCK_ITEMS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get()));
			ITEMS.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get().data));
		});
		event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> blockEntityTypes.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> entityTypes.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.PARTICLE_TYPES, helper -> particleTypes.forEach((identifier, supplier) -> helper.register(identifier.data, supplier.get())));
		event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> soundEvents.forEach(((identifier, supplier) -> helper.register(identifier.data, supplier.get().data))));
	}

	@SubscribeEvent
	public void buildContents(CreativeModeTabEvent.Register event) {
		creativeModeTabs.forEach(creativeModeTabHolder -> event.registerCreativeModeTab(creativeModeTabHolder.identifier, builder -> builder
				.title(Component.translatable(String.format("itemGroup.%s.%s", creativeModeTabHolder.identifier.getNamespace(), creativeModeTabHolder.identifier.getPath())))
				.icon(() -> creativeModeTabHolder.iconSupplier.get().data)
				.displayItems((params, output) -> creativeModeTabHolder.itemSuppliers.forEach(itemSupplier -> output.accept(itemSupplier.get().data)))
				.build()
		));
	}
}
