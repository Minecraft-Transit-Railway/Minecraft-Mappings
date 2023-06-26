package org.mtr.mapping.registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Registry implements ModInitializer {

	private static final Map<ResourceLocation, Block> BLOCKS = new HashMap<>();
	private static final Map<ResourceLocation, Item> ITEMS = new HashMap<>();
	private static final Map<ResourceLocation, net.minecraft.block.entity.BlockEntityType<? extends BlockEntity>> BLOCK_ENTITY_TYPES = new HashMap<>();
	private static final Map<ResourceLocation, ItemGroup> ITEM_GROUPS = new HashMap<>();

	@MappedMethod
	public static void init() {
	}

	@Override
	public void onInitialize() {
		BLOCKS.forEach((resourceLocation, block) -> net.minecraft.registry.Registry.register(Registries.BLOCK, resourceLocation.data, block));
		ITEMS.forEach((resourceLocation, item) -> net.minecraft.registry.Registry.register(Registries.ITEM, resourceLocation.data, item));
		BLOCK_ENTITY_TYPES.forEach((resourceLocation, item) -> net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, resourceLocation.data, item));
		ITEM_GROUPS.forEach((resourceLocation, itemGroup) -> net.minecraft.registry.Registry.register(Registries.ITEM_GROUP, resourceLocation.data, itemGroup));
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		return new BlockRegistryObject(register(BLOCKS, resourceLocation, supplier));
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		return new ItemRegistryObject(register(ITEMS, resourceLocation, supplier));
	}

	@MappedMethod
	public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = net.minecraft.block.entity.BlockEntityType.Builder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blocks).build(null);
		BLOCK_ENTITY_TYPES.put(resourceLocation, blockEntityType);
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	private static <T> T register(Map<ResourceLocation, T> map, ResourceLocation resourceLocation, Supplier<T> supplier) {
		final T data = supplier.get();
		map.put(resourceLocation, data);
		return data;
	}

	@MappedMethod
	public static CreativeModeTabHolder createCreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		final ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> iconSupplier.get().data).displayName(Text.translatable(String.format("itemGroup.%s.%s", resourceLocation.data.getNamespace(), resourceLocation.data.getPath()))).build();
		ITEM_GROUPS.put(resourceLocation, itemGroup);
		return new CreativeModeTabHolder(itemGroup, resourceLocation.data);
	}
}
