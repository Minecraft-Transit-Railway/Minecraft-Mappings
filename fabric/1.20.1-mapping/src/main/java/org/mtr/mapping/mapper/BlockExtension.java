package org.mtr.mapping.mapper;

import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@Deprecated
	@Override
	protected final void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.scheduleBlockTick(pos, block, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.getBlockTickScheduler().isQueued(pos.data, block.data);
	}
}
