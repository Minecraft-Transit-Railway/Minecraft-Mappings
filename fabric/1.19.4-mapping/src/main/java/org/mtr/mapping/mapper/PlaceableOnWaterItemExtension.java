package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.PlaceableOnWaterItemAbstractMapping;

public class PlaceableOnWaterItemExtension extends PlaceableOnWaterItemAbstractMapping implements ItemHelper {

	@MappedMethod
	public PlaceableOnWaterItemExtension(Block block, ItemSettings itemSettings) {
		super(block, itemSettings);
	}
}
