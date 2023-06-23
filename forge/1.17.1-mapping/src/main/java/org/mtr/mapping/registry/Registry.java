package org.mtr.mapping.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.Item;
import org.mtr.mapping.tool.Dummy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Registry extends Dummy {

	private static final Set<Supplier<Block>> BLOCKS = new HashSet<>();
	private static final Set<Supplier<Item>> ITEMS = new HashSet<>();
	private static final Set<Supplier<BlockEntityType<? extends BlockEntity>>> BLOCK_ENTITY_TYPES = new HashSet<>();

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<net.minecraft.world.level.block.Block> event) {
		BLOCKS.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<net.minecraft.world.item.Item> event) {
		ITEMS.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@SubscribeEvent
	public void registerBlockEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		BLOCK_ENTITY_TYPES.forEach(supplier -> event.getRegistry().register(supplier.get()));
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		BLOCKS.add(supplier);
		return new BlockRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		ITEMS.add(supplier);
		return new ItemRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		BLOCK_ENTITY_TYPES.add(() -> BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blocks).build(null));
		return new BlockEntityTypeRegistryObject<>(resourceLocation);
	}
}
