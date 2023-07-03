package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockRegistryObject extends RegistryObject<BlockExtension> {

	private final net.minecraftforge.fmllegacy.RegistryObject<BlockExtension> registryObject;

	BlockRegistryObject(ResourceLocation resourceLocation) {
		registryObject = net.minecraftforge.fmllegacy.RegistryObject.of(resourceLocation.data, ForgeRegistries.BLOCKS);
	}

	@MappedMethod
	@Override
	public BlockExtension get() {
		return registryObject.get();
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<BlockExtension> consumer) {
		registryObject.ifPresent(consumer);
	}
}
