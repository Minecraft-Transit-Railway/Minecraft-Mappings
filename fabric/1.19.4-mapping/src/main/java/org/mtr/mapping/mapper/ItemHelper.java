package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.registry.CreativeModeTabHolder;
import org.mtr.mapping.tool.DummyInterface;

public interface ItemHelper extends DummyInterface {

	final class Properties {

		final FabricItemSettings itemSettings;

		@MappedMethod
		public Properties() {
			this.itemSettings = new FabricItemSettings();
		}

		private Properties(FabricItemSettings itemSettings) {
			this.itemSettings = itemSettings;
		}

		@MappedMethod
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder) {
			return this;
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.maxCount(maxCount));
		}
	}
}
