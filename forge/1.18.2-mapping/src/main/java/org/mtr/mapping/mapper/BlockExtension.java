package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockAbstractMapping;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;

public abstract class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockHelper.Properties properties) {
		super(properties.blockSettings);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.scheduleTick(pos, block, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}
}
