package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
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
	protected final void appendProperties2(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip2(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltip, options);
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new Property<>(TYPE));
		properties.add(new Property<>(WATERLOGGED));
	}

	@MappedMethod
	public static SlabType getType(BlockState state) {
		return SlabType.convert(state.data.get(TYPE));
	}
}
