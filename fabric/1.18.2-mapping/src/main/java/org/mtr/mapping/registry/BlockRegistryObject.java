package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockRegistryObject extends RegistryObject<BlockExtension> {

	private final BlockExtension blockExtension;

	BlockRegistryObject(BlockExtension blockExtension) {
		this.blockExtension = blockExtension;
	}

	@MappedMethod
	@Override
	public BlockExtension get() {
		return blockExtension;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<BlockExtension> consumer) {
		consumer.accept(blockExtension);
	}
}
