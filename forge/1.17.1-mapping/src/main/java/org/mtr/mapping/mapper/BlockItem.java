package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;

public class BlockItem extends net.minecraft.world.item.BlockItem {

	@MappedMethod
	public BlockItem(Block block, Item.Properties properties) {
		super(block, properties.itemSettings);
	}
}
