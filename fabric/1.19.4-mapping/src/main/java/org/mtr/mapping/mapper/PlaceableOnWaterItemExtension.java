package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.PlaceableOnWaterItemAbstractMapping;

public class PlaceableOnWaterItemExtension extends PlaceableOnWaterItemAbstractMapping implements ItemHelper {

	@MappedMethod
	public PlaceableOnWaterItemExtension(Block block, Properties properties) {
		super(block, properties.itemSettings);
	}
}
