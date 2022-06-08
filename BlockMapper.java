package mtr.mappings;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockMapper extends Block {

	public BlockMapper(Properties properties) {
		super(properties);
	}

	@Override
	public final void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource randomSource) {
		super.tick(state, world, pos, randomSource);
		tick(state, world, pos);
	}

	@Override
	public final void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource randomSource) {
		super.randomTick(state, world, pos, randomSource);
		randomTick(state, world, pos);
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos) {
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos) {
	}
}
