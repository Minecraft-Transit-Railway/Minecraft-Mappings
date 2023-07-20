package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockEntityTypeRegistryObject<T extends BlockEntityExtension> extends RegistryObject<BlockEntityType<T>> {

	private final net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<T>> registryObject;

	BlockEntityTypeRegistryObject(Identifier identifier) {
		registryObject = net.minecraftforge.registries.RegistryObject.create(identifier.data, ForgeRegistries.BLOCK_ENTITY_TYPES);
	}

	@MappedMethod
	@Override
	public BlockEntityType<T> get() {
		return new BlockEntityType<>(registryObject.get());
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<BlockEntityType<T>> consumer) {
		registryObject.ifPresent(data -> consumer.accept(new BlockEntityType<>(data)));
	}
}
