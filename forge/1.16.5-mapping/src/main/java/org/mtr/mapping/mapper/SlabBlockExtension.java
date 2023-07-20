package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.SlabBlockAbstractMapping;

public class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public SlabBlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}
}
