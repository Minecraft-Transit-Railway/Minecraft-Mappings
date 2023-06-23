package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ResourceLocation;
import org.mtr.mapping.mapper.Item;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class ItemRegistryObject extends RegistryObject<Item> {

	private final net.minecraftforge.fmllegacy.RegistryObject<Item> registryObject;

	ItemRegistryObject(ResourceLocation resourceLocation) {
		registryObject = net.minecraftforge.fmllegacy.RegistryObject.of(resourceLocation.data, ForgeRegistries.ITEMS);
	}

	@MappedMethod
	@Override
	public Item get() {
		return registryObject.get();
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<Item> consumer) {
		registryObject.ifPresent(consumer);
	}
}
