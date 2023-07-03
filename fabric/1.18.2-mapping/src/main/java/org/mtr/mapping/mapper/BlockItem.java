package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;

public class BlockItem extends net.minecraft.item.BlockItem {

	@MappedMethod
	public BlockItem(BlockExtension blockExtension, ItemExtension.Properties properties) {
		super(blockExtension, properties.itemSettings);
	}
}
