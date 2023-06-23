package org.mtr.mapping.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.Item;
import org.mtr.mapping.tool.Dummy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Registry extends Dummy {

	private static final Map<ResourceLocation, Supplier<Block>> BLOCKS = new HashMap<>();
	private static final Map<ResourceLocation, Supplier<Item>> ITEMS = new HashMap<>();
	private static final Map<ResourceLocation, Supplier<BlockEntityType<? extends BlockEntity>>> BLOCK_ENTITY_TYPES = new HashMap<>();

	@SubscribeEvent
	public void registerBlocks(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BLOCKS, helper -> BLOCKS.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get())));
		event.register(ForgeRegistries.Keys.ITEMS, helper -> ITEMS.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get())));
		event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> BLOCK_ENTITY_TYPES.forEach((resourceLocation, supplier) -> helper.register(resourceLocation.data, supplier.get())));
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		BLOCKS.put(resourceLocation, supplier);
		return new BlockRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		ITEMS.put(resourceLocation, supplier);
		return new ItemRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		BLOCK_ENTITY_TYPES.put(resourceLocation, () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blocks).build(null));
		return new BlockEntityTypeRegistryObject<>(resourceLocation);
	}
}
