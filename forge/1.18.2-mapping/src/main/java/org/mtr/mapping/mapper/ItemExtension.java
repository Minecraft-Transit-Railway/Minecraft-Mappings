package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ItemAbstractMapping;
import org.mtr.mapping.registry.CreativeModeTabHolder;

public abstract class ItemExtension extends ItemAbstractMapping {

	public ItemExtension(Properties properties) {
		super(properties.itemSettings);
	}

	public static final class Properties {

		final net.minecraft.world.item.Item.Properties itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new net.minecraft.world.item.Item.Properties();
		}

		private Properties(net.minecraft.world.item.Item.Properties itemSettings) {
			this.itemSettings = itemSettings;
		}

		@MappedMethod
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder) {
			return new Properties(itemSettings.tab(creativeModeTabHolder.creativeModeTab));
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.stacksTo(maxCount));
		}
	}
}
