package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;

public class BlockItem extends net.minecraft.item.BlockItem implements ItemHelper {

	@MappedMethod
	public BlockItem(Block block, Properties properties) {
		super(block.data, properties.itemSettings);
	}
}