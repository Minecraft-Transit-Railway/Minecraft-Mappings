package org.mtr.mapping.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.BlockItem;
import org.mtr.mapping.mapper.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class ModEventBus {

	static final Map<ResourceLocation, Supplier<Block>> BLOCKS = new HashMap<>();
	static final Map<ResourceLocation, Supplier<BlockItem>> BLOCK_ITEMS = new HashMap<>();
	static final Map<ResourceLocation, Supplier<Item>> ITEMS = new HashMap<>();
	static final Map<ResourceLocation, Supplier<BlockEntityType<? extends BlockEntity>>> BLOCK_ENTITY_TYPES = new HashMap<>();
	static final Set<CreativeModeTabHolder> CREATIVE_MODE_TABS = new HashSet<>();

	@SubscribeEvent
	public static void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BLOCKS, helper -> BLOCKS.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get())));
		event.register(ForgeRegistries.Keys.ITEMS, helper -> {
			BLOCK_ITEMS.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get()));
			ITEMS.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get()));
		});
		event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> BLOCK_ENTITY_TYPES.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get())));
	}

	@SubscribeEvent
	public void buildContents(CreativeModeTabEvent.Register event) {
		CREATIVE_MODE_TABS.forEach(creativeModeTabHolder -> event.registerCreativeModeTab(creativeModeTabHolder.resourceLocation, builder -> builder
				.title(Component.translatable(String.format("itemGroup.%s.%s", creativeModeTabHolder.resourceLocation.getNamespace(), creativeModeTabHolder.resourceLocation.getPath())))
				.icon(() -> creativeModeTabHolder.iconSupplier.get().data)
				.displayItems((params, output) -> creativeModeTabHolder.itemSuppliers.forEach(itemSupplier -> output.accept(itemSupplier.get())))
				.build()
		));
	}
}
