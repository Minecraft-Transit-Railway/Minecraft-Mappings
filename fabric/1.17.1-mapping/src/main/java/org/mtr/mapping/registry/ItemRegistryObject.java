package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class ItemRegistryObject extends RegistryObject<ItemExtension> {

	private final ItemExtension itemExtension;

	ItemRegistryObject(ItemExtension itemExtension) {
		this.itemExtension = itemExtension;
	}

	@MappedMethod
	@Override
	public ItemExtension get() {
		return itemExtension;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<ItemExtension> consumer) {
		consumer.accept(itemExtension);
	}
}
