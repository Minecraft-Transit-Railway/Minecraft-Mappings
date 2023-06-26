package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.registry.CreativeModeTabHolder;

import java.util.function.Supplier;

public abstract class Item extends net.minecraft.item.Item {

	public Item(Properties properties) {
		super(properties.itemSettings);
	}

	public static final class Properties {

		final net.minecraft.item.Item.Properties itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new net.minecraft.item.Item.Properties();
		}

		private Properties(net.minecraft.item.Item.Properties itemSettings) {
			this.itemSettings = itemSettings;
		}

		@MappedMethod
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder, Supplier<Item> itemSupplier) {
			return new Properties(itemSettings.tab(creativeModeTabHolder.creativeModeTab));
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.stacksTo(maxCount));
		}
	}
}
