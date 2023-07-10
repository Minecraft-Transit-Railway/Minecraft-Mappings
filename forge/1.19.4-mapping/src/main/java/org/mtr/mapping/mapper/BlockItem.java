package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;

public class BlockItem extends net.minecraft.world.item.BlockItem implements ItemHelper {

	@MappedMethod
	public BlockItem(Block block, ItemHelper.Properties properties) {
		super(block.data, properties.itemSettings);
	}
}
