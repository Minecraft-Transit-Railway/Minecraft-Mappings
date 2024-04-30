package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
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
	protected final void appendProperties(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.item.ItemStack stack, @Nullable net.minecraft.world.BlockView world, List<Text> tooltip, net.minecraft.client.item.TooltipContext options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltip, new TooltipContext(options));
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
		return StairShape.convert(state.data.get(SHAPE));
	}
}
