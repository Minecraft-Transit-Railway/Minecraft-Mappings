package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockItemAbstractMapping;

public class BlockItemExtension extends BlockItemAbstractMapping implements ItemHelper {

	@MappedMethod
	public BlockItemExtension(Block block, ItemHelper.Properties properties) {
		super(block, properties.itemSettings);
	}
}
