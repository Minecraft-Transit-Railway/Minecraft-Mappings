package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.registry.CreativeModeTabHolder;

import java.util.function.Supplier;

public abstract class Item extends net.minecraft.item.Item {

	public Item(Properties properties) {
		super(properties.itemSettings);
	}

	public static final class Properties {

		final FabricItemSettings itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new FabricItemSettings();
		}

		private Properties(FabricItemSettings itemSettings) {
			this.itemSettings = itemSettings;
		}

		@MappedMethod
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder, Supplier<Item> itemSupplier) {
			return new Properties(itemSettings.group(creativeModeTabHolder.creativeModeTab));
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.maxCount(maxCount));
		}
	}
}
