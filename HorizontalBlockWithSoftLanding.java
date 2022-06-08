package @package@;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HorizontalBlockWithSoftLanding extends BlockDirectionalMapper {

	public HorizontalBlockWithSoftLanding(Properties settings) {
		super(settings);
	}

	@Override
	public final void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float distance) {
		super.fallOn(world, state, pos, entity, distance * (softenLanding() ? 0.5F : 1));
	}

	public boolean softenLanding() {
		return false;
	}
}
