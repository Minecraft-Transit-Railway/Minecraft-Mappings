package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockEntityTypeRegistryObject<T extends BlockEntityExtension> extends RegistryObject<BlockEntityType<T>> {

	private final BlockEntityType<T> blockEntityType;

	BlockEntityTypeRegistryObject(BlockEntityType<T> blockEntityType) {
		this.blockEntityType = blockEntityType;
	}

	@MappedMethod
	@Override
	public BlockEntityType<T> get() {
		return blockEntityType;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<BlockEntityType<T>> consumer) {
		consumer.accept(blockEntityType);
	}
}
