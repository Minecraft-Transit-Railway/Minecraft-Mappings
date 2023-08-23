package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.PlayerInventory;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class PlayerHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static PlayerInventory getPlayerInventory(@Nullable PlayerEntity playerEntity) {
		return playerEntity == null ? null : new PlayerInventory(playerEntity.data.inventory);
	}
}
