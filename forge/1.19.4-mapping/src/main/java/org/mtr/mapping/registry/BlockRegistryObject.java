package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockRegistryObject extends RegistryObject<Block> {

	private final net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.Block> registryObject;

	BlockRegistryObject(Identifier identifier) {
		registryObject = net.minecraftforge.registries.RegistryObject.create(identifier.data, ForgeRegistries.BLOCKS);
	}

	@MappedMethod
	@Override
	public Block get() {
		return new Block(registryObject.get());
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<Block> consumer) {
		registryObject.ifPresent(block -> consumer.accept(new Block(block)));
	}
}
