package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.ITextComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;

public class StairsBlockExtension extends StairsBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public StairsBlockExtension(BlockState baseBlockState, BlockSettings blockSettings) {
		super(baseBlockState, blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition2(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText2(ItemStack stack, @Nullable BlockView world, List<ITextComponent> tooltipList, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltipList, options);
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new Property<>(FACING));
		properties.add(new Property<>(HALF));
		properties.add(new Property<>(SHAPE));
		properties.add(new Property<>(WATERLOGGED));
	}

	@MappedMethod
	public static StairShape getType(BlockState state) {
		return StairShape.convert(state.data.getValue(SHAPE));
	}
}
