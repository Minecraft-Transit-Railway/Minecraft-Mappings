package org.mtr.mapping.registry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.BlockItem;
import org.mtr.mapping.mapper.Item;
import org.mtr.mapping.tool.Dummy;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class Registry extends Dummy {

	@MappedMethod
	public static void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBus.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBus.class);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.put(resourceLocation, supplier);
		return new BlockRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlockWithBlockItem(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.put(resourceLocation, supplier);
		final BlockRegistryObject blockRegistryObject = new BlockRegistryObject(resourceLocation);
		ModEventBus.BLOCK_ITEMS.put(resourceLocation, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()));
		return blockRegistryObject;
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		ModEventBus.ITEMS.put(resourceLocation, supplier);
		return new ItemRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		ModEventBus.BLOCK_ENTITY_TYPES.put(resourceLocation, () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blocks).build(null));
		return new BlockEntityTypeRegistryObject<>(resourceLocation);
	}

	@MappedMethod
	public static CreativeModeTabHolder createCreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		final CreativeModeTabHolder creativeModeTabHolder = new CreativeModeTabHolder(resourceLocation.data, iconSupplier);
		ModEventBus.CREATIVE_MODE_TABS.add(creativeModeTabHolder);
		return creativeModeTabHolder;
	}
}
