package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
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
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable BlockGetter world, List<Component> tooltipList, TooltipFlag options) {
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
