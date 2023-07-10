package org.mtr.mapping.mapper;

import net.minecraft.world.item.Item;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.registry.CreativeModeTabHolder;
import org.mtr.mapping.tool.DummyInterface;

public interface ItemHelper extends DummyInterface {

	final class Properties {

		final Item.Properties itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new Item.Properties();
		}

		private Properties(Item.Properties itemSettings) {
			this.itemSettings = itemSettings;
		}

		@MappedMethod
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder) {
			return this;
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.stacksTo(maxCount));
		}
	}
}
