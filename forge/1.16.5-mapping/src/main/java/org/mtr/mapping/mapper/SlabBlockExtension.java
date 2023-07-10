package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.SlabBlockAbstractMapping;

public abstract class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public SlabBlockExtension(BlockHelper.Properties properties) {
		super(properties.blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}
}
