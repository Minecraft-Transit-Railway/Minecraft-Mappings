package org.mtr.mapping.registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ResourceLocation;
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

	@Override
	public void onInitialize() {
		BLOCKS.forEach((resourceLocation, block) -> net.minecraft.registry.Registry.register(Registries.BLOCK, resourceLocation.data, block));
		ITEMS.forEach((resourceLocation, item) -> net.minecraft.registry.Registry.register(Registries.ITEM, resourceLocation.data, item));
		BLOCK_ENTITY_TYPES.forEach((resourceLocation, item) -> net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, resourceLocation.data, item));
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
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blocks).build(null);
		BLOCK_ENTITY_TYPES.put(resourceLocation, blockEntityType);
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	private static <T> T register(Map<ResourceLocation, T> map, ResourceLocation resourceLocation, Supplier<T> supplier) {
		final T data = supplier.get();
		map.put(resourceLocation, data);
		return data;
	}
}
