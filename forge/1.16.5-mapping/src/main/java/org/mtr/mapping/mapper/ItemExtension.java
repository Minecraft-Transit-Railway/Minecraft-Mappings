package org.mtr.mapping.mapper;

import org.mtr.mapping.holder.ItemAbstractMapping;

public abstract class ItemExtension extends ItemAbstractMapping implements ItemHelper {

	public ItemExtension(ItemHelper.Properties properties) {
		super(properties.itemSettings);
	}
}
