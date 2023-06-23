package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;

public abstract class Item extends net.minecraft.item.Item {

	public Item(Properties properties) {
		super(properties.itemSettings);
	}

	public static class Properties {

		protected final net.minecraft.item.Item.Properties itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new net.minecraft.item.Item.Properties();
		}
	}
}
