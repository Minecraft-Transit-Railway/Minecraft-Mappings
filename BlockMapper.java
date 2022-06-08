package @package@;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public abstract class BlockMapper extends Block {

	public BlockMapper(Properties properties) {
		super(properties);
	}

	@Override
	public final void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		super.tick(state, world, pos, random);
		tick(state, world, pos);
	}

	@Override
	public final void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		randomTick(state, world, pos);
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos) {
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos) {
	}
}
