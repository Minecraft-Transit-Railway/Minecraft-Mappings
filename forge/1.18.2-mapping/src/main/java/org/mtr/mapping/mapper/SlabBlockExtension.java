package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public SlabBlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition2(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new Property<>(TYPE));
		properties.add(new Property<>(WATERLOGGED));
	}

	@MappedMethod
	public static SlabType getType(BlockState state) {
		return SlabType.convert(state.data.getValue(TYPE));
	}
}
