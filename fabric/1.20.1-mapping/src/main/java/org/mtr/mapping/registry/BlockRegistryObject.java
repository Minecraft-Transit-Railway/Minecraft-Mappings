package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockRegistryObject extends RegistryObject<Block> {

	private final Block block;

	BlockRegistryObject(Block block) {
		this.block = block;
	}

	@MappedMethod
	@Override
	public Block get() {
		return block;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<Block> consumer) {
		consumer.accept(block);
	}
}
