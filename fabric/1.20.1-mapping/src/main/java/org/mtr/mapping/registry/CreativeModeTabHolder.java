package org.mtr.mapping.registry;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public final class CreativeModeTabHolder {

	public final ItemGroup creativeModeTab;
	public final Identifier resourceLocation;

	public CreativeModeTabHolder(ItemGroup creativeModeTab, Identifier resourceLocation) {
		this.creativeModeTab = creativeModeTab;
		this.resourceLocation = resourceLocation;
	}
}
