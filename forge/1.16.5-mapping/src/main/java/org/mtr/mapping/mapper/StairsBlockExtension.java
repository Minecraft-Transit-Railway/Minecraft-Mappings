package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
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
	protected final void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.item.ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltipList, ITooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltipList, new TooltipContext(options));
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
