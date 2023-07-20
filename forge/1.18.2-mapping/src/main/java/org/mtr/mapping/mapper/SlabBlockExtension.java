package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}
}
