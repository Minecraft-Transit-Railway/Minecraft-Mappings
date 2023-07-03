package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ItemAbstractMapping;
import org.mtr.mapping.registry.CreativeModeTabHolder;

import java.util.function.Supplier;

public abstract class ItemExtension extends ItemAbstractMapping {

	public ItemExtension(Properties properties) {
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
		public Properties creativeModeTab(CreativeModeTabHolder creativeModeTabHolder, Supplier<ItemExtension> itemSupplier) {
			ItemGroupEvents.modifyEntriesEvent(creativeModeTabHolder.creativeModeTab).register(content -> content.add(itemSupplier.get()));
			return this;
		}

		@MappedMethod
		public Properties maxCount(int maxCount) {
			return new Properties(itemSettings.maxCount(maxCount));
		}
	}
}
