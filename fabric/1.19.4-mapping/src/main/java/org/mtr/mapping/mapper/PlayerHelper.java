package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.PlayerInventory;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public final class PlayerHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static PlayerInventory getPlayerInventory(@Nullable PlayerEntity playerEntity) {
		return playerEntity == null ? null : new PlayerInventory(playerEntity.data.getInventory());
	}

	@MappedMethod
	public static boolean isHolding(@Nullable PlayerEntity playerEntity, Predicate<Item> predicate) {
		return playerEntity != null && playerEntity.data.isHolding(itemStack -> predicate.test(new Item(itemStack.getItem())));
	}
}
