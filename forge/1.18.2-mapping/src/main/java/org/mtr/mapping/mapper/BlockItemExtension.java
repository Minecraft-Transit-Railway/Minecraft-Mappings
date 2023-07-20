package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockItemAbstractMapping;
import org.mtr.mapping.holder.ItemSettings;

public class BlockItemExtension extends BlockItemAbstractMapping {

	@MappedMethod
	public BlockItemExtension(Block block, ItemSettings itemSettings) {
		super(block, itemSettings);
	}
}
