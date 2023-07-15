package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.SlabBlockAbstractMapping;

public class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public SlabBlockExtension(Properties properties) {
		super(properties.blockSettings);
	}

	@Deprecated
	@Override
	protected final void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		appendPropertiesHelper(builder);
	}
}
