package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class BlockEntityTypeRegistryObject<T extends BlockEntity> extends RegistryObject<BlockEntityType<T>> {

	private final net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<T>> registryObject;

	BlockEntityTypeRegistryObject(ResourceLocation resourceLocation) {
		registryObject = net.minecraftforge.registries.RegistryObject.create(resourceLocation.data, ForgeRegistries.BLOCK_ENTITIES);
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
