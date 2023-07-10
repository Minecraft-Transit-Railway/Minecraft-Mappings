package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class ItemRegistryObject extends RegistryObject<Item> {

	private final Item item;

	ItemRegistryObject(Item item) {
		this.item = item;
	}

	@MappedMethod
	@Override
	public Item get() {
		return item;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<Item> consumer) {
		consumer.accept(item);
	}
}
