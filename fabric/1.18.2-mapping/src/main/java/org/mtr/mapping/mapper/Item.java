package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class Item extends net.minecraft.item.Item {

	public Item(Properties properties) {
		super(properties.itemSettings);
	}

	public static class Properties {

		protected final FabricItemSettings itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new FabricItemSettings();
		}
	}
}
