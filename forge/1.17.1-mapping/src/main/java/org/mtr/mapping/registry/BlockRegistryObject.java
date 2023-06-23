package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockRegistryObject extends RegistryObject<Block> {

	private final net.minecraftforge.fmllegacy.RegistryObject<Block> registryObject;

	BlockRegistryObject(ResourceLocation resourceLocation) {
		registryObject = net.minecraftforge.fmllegacy.RegistryObject.of(resourceLocation.data, ForgeRegistries.BLOCKS);
	}

	@MappedMethod
	@Override
	public Block get() {
		return registryObject.get();
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<Block> consumer) {
		registryObject.ifPresent(consumer);
	}
}
